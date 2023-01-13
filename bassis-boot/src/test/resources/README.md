test
---------------------------

### 采用`bean`动态代理处理-返回`json`数据
```
curl -X POST http://localhost:10001/api/user/add

curl -X GET http://localhost:10001/api/user/add
```

### 采用`http`静态代理文件-返回页面
```
curl -X GET http://localhost:10001/api/index

curl -X GET http://localhost:10001/api/404

curl -X GET http://localhost:10001/api/500

curl -X GET http://localhost:10001/api/503
```