## RobotServer
儿童机器人服务器端的框架、组件、标准接口、维护系统、打点系统

## RobotServer架构说明
- 基于spring + paoding-rose
- 支持redis缓存

## RobotServer功能说明
- 支持照片的剪辑、合成等处理
- 集成SMS服务，可选配腾讯、百悟等厂商的SMS服务
- 集成音视频服务，可选配腾讯、AGORA等厂商的音视频服务
- 集成语音服务，可选配科大讯飞的语音服务
- 集成语义服务，可选配图灵机器人的语义服务

## RobotServer定制说明
**1. /src/main/resources/app.properties
- 可定制参数：http请求的连接超时时间、socket超时时间
- 可定制参数：http上传文件的大小

**2./src/main/resources/key.properties
- 可定制参数：数据库连接url、用户名、口令等安全信息
- 可定制参数：redis ip、端口、口令等安全信息，以及超时时间等配置信息
