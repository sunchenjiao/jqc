# 项目简介
JQC是一个Java写的分布式的拉取式的消息队列中间件，具备高并发性和可扩展性，主要用于解决C/S架构消息传递的问题。以HTTP接口的形式提供调用，最大限度的降低开发成本。
# 功能介绍
## 角色
JQC中存在4种角色   
- **Producer**：消息产生者，也就是用户。使用接口来产生消息。   
- **JobTracker**：调度中心，用于处理各种请求，包括创建消息、消费消息等等。JobTracker是平等的，可存在多个，支持任意扩展。   
-** Admin**：管理员，用于监控集群状态，处理消息超时和回收机制、节点宕机报警等。当前版本Admin只能存在一个。   
-**TaskTracker**：消费者，通过接口来拉取消息和反馈结果。

## 组件
JQC用到了3个组件：Nginx、Redis、ElasticSearch   
- **Nginx**：当前版本使用Nginx作为集群入口和请求转发、负载均衡。   
- **Redis**：Redis用于存储消息队列和节点信息，当前版本Redis采用了单节点的简单模式。   
- **ElasticSearch**：用于存储历史消息和查询。

## 支持功能
- **消息拉取模式**：单向通信，TaskTracker根据自身需要主动向JobTracker请求拉取消息。这样TaskTracker可自己维护自己的负载情况。
- **异步请求**：消息被创建后，Producer得到消息的id，并通过id来轮询消息的状态和结果。
- **优先级**：支持0-4共5个不同的优先级(0最高)。
- **先进先出**：同一优先级的消息先进先出。
- **自定义消息队列(Point类型)**：Producer指定消息存在在某个名称的队列中，TaskTracker可去该名称的队列中拉取消息。
- **定向分配(Compete类型)**：即TaskTracker的群组功能。举例：TaskTracker1和TaskTracker2属于GroupA，TaskTracker3和TaskTracker4属于GroupB。Producer在产生消息的时候可以指定该消息能被GroupA(支持多个Group)消费，则Tasktracker1和TaskTracker2都可以获取该消息，谁先来拉取，谁获得(消息仅能被消费1次)。
- **超时机制**：JQC采取了最简单的消息超时机制：每条消息被消费后必须在指定时间(消息属性可设置)反馈结果，否则将被Admin检测之后回收处理(超时处理或重试处理)。
-** 重试机制**：消息可以设置重试次数，当超时后重新放回队列中等待被取走。
- **有效时间机制**：消息被创建后可以设置有效时间，超过有效时间仍未被取走则会被废弃。
- **节点监控**：TaskTracker有注册机制，并通过心跳信息维持自身可用性信息、JobTracker、Admin也会周期性的刷新自身状态。Admin可以监控到整个集群的状态，感知到节点宕机并通知。

# 使用方法
##1.创建JOB
*前提说明*
> 目前JQC创建JOB支持API方式直接创建，无需其他前提操作如登录，验证密码，token等。
###2.1 注册TASKTRACKER
**Path** :`/job/create`   
**Method**:`post`   
**ParamType**:`FormParam(ContentType:application/x-www-form-urlencoded)`  
**Params**:   

	product:产品线名称，必填（比如：mvp，pluto）。  
	topic: job放置的队列名称，必填（比如：sports，music）。
	type: job类型，缺省值为POINT（比如：POINT,COMPETE）。
	priority：任务优先级，缺省值为1（0为最高优先级），范围0~4。
	name：job名。
	data：job携带的具体数据，必填.
	expiration：过期时间(单位秒)，从Job进入待分配队列开始算起，若被取走时已经超过了设置的过期时间，则不会被取走，被认定为已过期失效。缺省值为0，代表永不过期。
	timeout：超时时间(单位秒)，从Job被取走开始算起，若超过了超时时间仍未返回结果，则认定为执行超时，配合retryCount进行重试。缺省值为300秒。
	sender：发送者（比如：mayaming）
	topicGroup：当type为PUBLISH时的必填项，指定Job复制和放置到哪些topic群组所包含的topic队列里。
	consumeGroup：当type为COMPETE时的必填项，指定Job定向分配给哪些消费者群组。
	retryCount：当Job执行超时时，可重试的次数。缺省值为0，不重试。
	info：用户自定义字段，留待后续开发使用。



	
**Response**：


	{
	 	"message": "OK",（请求的结果）
 		 "status": true,（请求结果的状态）
 		 "data": {
			"jobId": "2a0988b7965d4d428eac08f98b33086e"（请求的结果中的数据）
 		 }
	}

*返回值说明*

>  status: 接口调用成功返回true，否则返回false  
>  message: 接口调用请求的结果   
>  data: 创建的JOB的uuid，也是该JOB的唯一标志字段   

##2.TASKTRACKER与JOBTRACKER通信
*前提说明*
> 拉取JOB和返回JOB结果之前，TASKTRACKER需要使用账号和密码进行注册操作（详见接口2.1）。        
> TASKTRACKER通过控制心跳中的参数来实现拉取JOB和返回JOB结果（详见接口2.2）

###2.1 注册TASKTRACKER
**Path** :`/taskTracker/register`   
**Method**:`post`    
**ParamType**:` json格式的String`    
**Params**:   

	{	 
		"identity":"EPtest", TASKTRACKER账号，唯一
		"passwd":"0385B200C56Fxxx",  密码
		"version":"1.0.0", 该TASKTRACKER的版本号
		"consumeGroup":"baidu", TASKTRACKER所属群组（拉取COMPETE类型JOB的话，是必填参数）。
		"ip":"172.18.23.xx", 节点所属IP地址
		"hostname":"BDSY00540", 节点所属hostname
		"info":"" 节点的附加信息，注册后可修改
	}
**Response**：


	{
    	"message":"OK",
		"status":true,
		"data":"e71f4a81c0ee4641913c183e84a18347"
	}

*返回值说明*
>  status: 接口调用成功返回true，否则返回false  
>  message: 接口调用请求的结果  
>  data： token信息，每次注册后JOBTRACKER将返回token信息，此后与JOBTRACKER通信只需要携带账号和token信息。但是如果超时未与JOBTRACKER通信，该TASKTRACKER将会被判定为掉线，此token也将会失效，即下次通信需要再次执行注册动作，获取新的token。

###2.2 发送心跳，拉取任务与返回任务结果
**Path** :`/taskTracker/heartbeat`   
**Method**:`post`  
**ParamType**:` json格式的String`     
**Params**: `目的不同，以下参数略有不同`  
*心跳说明*
> 在无需拉取任务也无需返回任务结果时，通过下面的参数与JOBTRACKER通信，保证该TASKTRACKER的在线状态。

	{	
		"identity":"EPtest", 账号，必填
		"passwd":"0385B200C56Fxxx",
		"token":"5f5eac64b93d4df3a30599e602fae9e2",	注册后返回的token信息，必填
		"version":"1.0.0",
		"consumeGroup":"baidu", 注册时填写的TASKTRACKER所属群组参数
		"ip":"172.18.23.xx",
		"hostname":"BDSY0054xxx",
		"ackJobs":[],
		"info":"xxx"
	}


**Response**：

	{
		"message":"OK",
		"status":true,
		"data":[]
	}

*返回值说明*
>  status: 接口调用成功返回true，否则返回false   
>  message: 接口调用请求的结果   
>  data： 如果没有请求拉取JOB，该字段为空，否则为请求的JOB数组。

----------

*拉取JOB说明*
> TASKTRACKER注册后，才可以拉取任务。

	{	
		"identity":"EPtest", 账号，必填
		"passwd":"0385B200C56Fxxx",
		"token":"5f5eac64b93d4df3a30599e602fae9e2",	注册后返回的token信息，必填
		"product":"mvp",  期望从这个业务线拉取任务，必填
		"topic":"sports", 获取的任务的topic，必填
		"version":"1.0.0",
		"consumeGroup":"baidu", 注册时填写的TASKTRACKER所属群组参数，拉取COMPETE类型的任务时候为必填
		"ip":"172.18.23.xx",
		"hostname":"BDSY0054xxx",
		"isExclusive":false, 如果为true，则只请求属于该群组的任务；否则不限制。
		"consumeType":"POINT", 本次请求的任务的类型，POINT（点到点）或COMPETE（竞争）
		"consumeNum":2 期望拿到的JOB量
	}
	
**Response**：

	{
		"message": "OK",
    	"status": true,
   		"data": [
        {
            "product": "mvp", 创建JOB时指定的产品线
            "topic": "CompatibilityTest", 创建JOB时指定的topic
            "priority": 1, 创建JOB时指定的优先级
            "uuid": "d171d0966d7e4xxxccc6074daed", 即jobId
            "type": "POINT", JOB的类型
            "status": "consumed", JOB的状态。ready-已创建，还未被拉取；consumed-已被拉取；acknowledged-已经返回结果
            "sendTimestamp": 1510300995173, 创建JOB的时间戳
            "consumeTimestamp": 1510736526967, 拉取JOB的时间戳
            "name": "test", 创建JOB时指定的名字
            "data": "{xxxxxx}", 存储需要被执行的详情
            "expiration": 0, 
            "timeout": 300000, 
            "sender": "xulinwei", 创建JOB的人
            "consumer": "EPtest", 拉取JOB的人
            "retryCount": 0, 
            "isDeleted": 0,
            "oriTimeout": 300000 
        }，{
            "product": "mvp",
            "topic": "CompatibilityTest",
            "priority": 1,
            "uuid": "d171d0966xxx3975ccc6074daed",
            "type": "POINT",
            "status": "consumed",
            "sendTimestamp": 1510300995173,
            "consumeTimestamp": 1510736526967,
            "name": "test",
            "data": "{xxxxxx}",
            "expiration": 0,
            "timeout": 300000,
            "sender": "xulinwei",
            "consumer": "EPtest",
            "retryCount": 0,
            "isDeleted": 0,
            "oriTimeout": 300000
        }
    	]
	}

*返回值说明*
>  status: 接口调用成功返回true，否则返回false  
>  message: 接口调用请求的结果  
>  data： 如果通过心跳拉取JOB，则data字段是拉取的JOB清单，以数组方式存储。

----------

*返回JOB结果说明*
> TASKTRACKER注册后，才可以返回任务结果。

	{	
		"identity":"EPtest", 账号，必填
		"passwd":"0385B200C56Fxxx",
		"token":"5f5eac64b93d4df3a30599e602fae9e2",	注册后返回的token信息，必填
		"version":"1.0.0",
		"consumeGroup":"baidu", 注册时填写的TASKTRACKER所属群组参数
		"ip":"172.18.23.xx",
		"hostname":"BDSY0054xxx",
		"ackJobs":[
			{"uuid":"1721f436131e4xxxa1888326eae","result":"xxxxxxxx"},
			{"uuid":"835ef436fbe04xxx988c25732f3","result":"12345654321"}
		],
		"info":"xxx"
	}
	
**Response**：

	{
		"message":"OK",
		"status":true,
		"data":[]
	}

*返回值说明*
>  status: 接口调用成功返回true，否则返回false   
>  message: 接口调用请求的结果   
>  data： 如果没有请求拉取JOB，该字段为空，否则为请求的JOB数组。

# 代码结构
*com.baidu.ecomqaep.schedule.admin*   

	AdminHeartbeatThread:Admin节点专用发送心跳线程   
 	CheckJobThread：监控是否有超时JOB   
 	CheckNodeThread：监控是否有超时TASKTRACKER和JOBTRACKER   
 	InsertEsThread：将已完成JOB从Redis刷进Es   
*com.baidu.ecomqaep.schedule.base*   
 
	AckEntity: JOB返回结果中ackJobs字段的基本单位   
 	ConsumeEntity：拉取任务请求的基本单位   
 	JobEntity：JOB的数据结构   
 	JobResultEntity：查询JOB接口的返回的数据结构   
 	NodeEntity：节点（包括TASKTRACKER,JOBTRACKER,ADMIN）的数据结构   
	 ResultType：接口返回的数据结构。   
 	StatusCode：ResultType中statusCode字段的库。   
 	TaskTracker2JobTrackerModel：TASKTRACKER与JOBTRACKER通信的数据结构   
 	TopicGroupEntity：订阅topicGroup所需的数据结构   
*com.baidu.ecomqaep.schedule.config*   
  
	BaseConfiguration：参数相关类   
	Config：固定参数配置，包括redis地址，es地址等   
	ConfigInterface： 参数相关类   
	 Constants：固定参数   
*com.baidu.ecomqaep.schedule.jobTracker*   
  
	JobTrackerHeartBeatThread：节点专用发送心跳线程   
*com.baidu.ecomqaep.schedule.listner*  
 	 
	ApplicationContextAwareListner:spring启动类   
com.baidu.ecomqaep.schedule.log   
  
	LogFormat：日志模板文件   

*com.baidu.ecomqaep.schedule.manager*   
  
	AdminManager：Admin逻辑处理类   
	 EsManager：ES处理类   
 	JobManager：消息处理类   
	 NodeManagre：节点处理类   
 	RedisManager：redis处理类   
 	TopicManager：群组处理类   
*com.baidu.ecomqaep.schedule.util*   
  
	IdUtil：唯一性id生成工具类   
 	MailUtil：邮件工具类   
	 Md5Util：MD5工具类   
	 ShortMessageUtil：短信工具类   

*com.baidu.ecomqaep.schedule.web.action*   
  
	AdminAction： Admin角色相关的接口   
	 BaseAction： 接口基类   
	 BulletinAction： Redis存储能力开放接口   
	 JobAction：消息处理相关接口   
 	TaskTrackAction：taskTracker相关接口   

*com.baidu.ecomqaep.schedule.web.filter*   
  
	RequestFilter：请求过滤类   
 	ResponseFilter：响应过滤类   

# 版本记录
## 1.0：JQC正式开源