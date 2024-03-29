# SQL映射/生成框架
### 项目介绍
> 由于目前JPA/hibernate框架数据绑定过于重量，小型项目采用多层分层开发效率不高且学习成功过高。
然mybatis框架虽然灵活且用户量较多，但其二次开发的框架基本参照JPA的功能进行封闭，个别功能不太
满足快速开发及快速入门的要求，XML映射不易排查问题。目前mybatis所支持的注解方式映射暂时未能支
持跟XML一样能力，且这类框架还是感觉不够轻量及灵活。

> 对于个别超小的项目，其实只需要一个Bean类映射功能，sql映射生成功能，sql执行功能，自定义sql
及VO映射功能（类似于HQL中的new语法）。这几块功能mybatis都具备，但都需要编写sql或在编码阶段
对代码进行生成，现希望框架能在启动时通过bean类上的注解，自动生成所需的sql并在执行对应语法时
自动执行并把结果映射回对应对象中。

> 针对分页，统计功能上，需对一对多分页进行封装，支持一对多映射分页并对count进行统一处理，方便
用户直接进行使用，不用过多考虑该类问题。统计上，采用自定义注解，如：@LeftJoin@count,@sum,@group等注解
采用VO类中增加注解，指定关联PO类，自动查询统计出对应VO数据。

> 条件方面类似example查询方式，可动态拼接条件。


### 项目功能说明

* 支持JPA基础注解映射到数据库表
* 支持解析JPA注解生成表的基础增/删/改/查 操作
* 支持配置执行sql输出打印
* 支持JPA一对多，多对一，注解，不支持多对多
* 支持生成一对多，多对一查询SQL
* 支持字段名映射策略配置，默认驼峰转换
* 支持通过VO进行查询映射，默认采用PO映射，非同名时需采用@name指定PO中字段名,@Po映射对应PO
* VO查询映射字段增加@count,@sum注解，类上增加@LeftJoin,@group,@RigthJoin,@Po,@Join注解支持
* 查询条件采用example方式进行拼接
* 支持对常用sql进行缓存，无需频繁生成


### 项目开发步骤:

- [X] 基础框架搭建，采用lombok
- [X] 支持JPA基础注解映射到数据库表
- [X] 支持解析JPA注解生成表的基础增/删/改/查 操作
- [X] 支持配置执行sql输出打印
- [X] 支持JPA一对多，多对一，注解，不支持多对多
- [X] 支持生成一对多，多对一查询SQL
- [X] 处理异常，如字段错误，删除只能删除po等，增加自定义异常
- [X] 支持字段名映射策略配置，默认驼峰转换
- [X] 支持通过VO进行查询映射，默认采用PO映射，非同名时需采用@name指定PO中字段名,@Po映射对应PO
- [X] VO查询映射字段增加@count,@sum注解，类上增加@LeftJoin,@group,@RigthJoin,@Po,@Join注解支持
- [X] 查询条件采用example方式进行拼接
- [X] 支持对常用sql进行缓存，无需频繁生成

### 支持数据库

| 名称 | 版本 | 是否支持          |
| ---- | ------- | ------------------ |
| mysql| 5.6.x   | :white_check_mark: |
| oracle| x.x.x   | :x:               |
| sqlserver| x.x.x   | :x:               |

### 计划中功能
- [ ] 支持Mongodb非关系型数据库
- [ ] 支持异步查询逻辑
- [ ] 支持通过maven-plugin进行编辑时生成SQL，使运行时性能更优
- [ ] 支持mybatis框架集成

### [点击查看spring-boot整合文档](https://github.com/scrollsyou/sql_help/blob/master/sql-help-spring-boot-starter/README.md)
### [点击查看详细功能文档](https://github.com/scrollsyou/sql_help/blob/master/sql-help-code/README.md)
