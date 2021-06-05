## sql-help-spring-boot-starter集成说明

### maven依赖：
```xml
<dependency>
    <groupId>io.github.scrollsyou</groupId>
    <artifactId>sql-help-spring-boot-starter</artifactId>
    <version>0.0.1</version>
</dependency>
```
### spring项目中开始使用
1. 在spring启动配置中增加@EnableSqlHelp开启框架功能  
如：
```
/**
 * 启动程序
 * 
 * @author qinghong
 */
@SpringBootApplication
@EnableSqlHelp
public class QinghongApplication
{
    public static void main(String[] args)
    {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(QinghongApplication.class, args);
        System.out.println("##########  启动完成   ##########");
    }
    .
    .
    .
```

2. 增加PO与数据库映射类  
* 声明在PO类上的注解：@Entity(tableName = "数据库表名，若驼峰命名后与类名一致此属性可不设置此属性")  
* 声明在PO类属性上的注解：  
    * @Id(name="主键ID列名，若驼峰命名后与PO类属性名一致此属性可不设置此属性", strategy = 主键策略，使用enum类GenerationType)  
    * @Column(name = "数据库表的列名，若驼峰命名后与PO类属性名一致此属性可不设置此属性", comments = "注释", dateType = "在数据库中的类型")  
    * @Version(strategy = 版本策略，使用enum类VersionGenerationType)：标记为版本号字段  
    * @Transient：标记为非数据库字段  
如：
```java
@Entity
public class SysUser
{
	@Transient
    private static final long serialVersionUID = 1L;

    /** 用户ID */
	@Id
    private Long id;

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;
.
.
.

@Entity
public class SysUserDept
{
	@Transient
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long userId;

    /** $column.columnComment */
    private Long deptId;
.
.
.

@Entity
public class SysDept
{
	@Transient
    private static final long serialVersionUID = 1L;

    /** 部门ID */
	@Id
    private Long id;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;
.
.
.
```
3. 增加VO与PO映射类进行查询操作  
如：
```java
@VOBean(mainPo = SysUser.class, entityAlias = "u")
@Join(po = SysUserDept.class, entityAlias = "a", joinConditions = "{a.userId}= {u.id}")
@Join(po = SysDept.class, entityAlias = "d", joinConditions = "{a.deptId} = {d.id}")
public class SelectUserListVo {

	private Long id;
	private String nickName;
	private String userName;
	private String email;
	private String avatar;
	private String phonenumber;
	private String password;
	private String sex;
	private String status;
	private String delFlag;
	private String loginIp;
	private Date loginDate;
	@OneToMany(tagerClass = Dept.class)
	private List<Dept> depts;
	
	public static class Dept{
		@PropertyMapping(originalName = "d.deptName")
		private String deptName;
		@PropertyMapping(originalName = "d.leader")
		private String leader;
		public String getDeptName() {
			return deptName;
		}
		public void setDeptName(String deptName) {
			this.deptName = deptName;
		}
		public String getLeader() {
			return leader;
		}
		public void setLeader(String leader) {
			this.leader = leader;
		}
		
	}
省略get/set方法
```
4. 通过注入SqlHelpBaseDao类进行数据库操作,通过dao进行bean操作
```java
@Resource
private SqlHelpBaseDao dao;

public void test(){
	Example example = ExampleImpl.newInstance();
	example.condition("1=1");
	log.info(JSON.toJSONString(dao.findAll(example, SelectUserListVo.class)));
}
```
打印结果为：
```json
[
    {
        "avatar":"/profile/avatar/2020/06/09/2023bfcabc4c40a19f7d78e1e1a633f3.jpeg",
        "delFlag":"0",
        "depts":[
            {
                "deptName":"XX科技",
                "leader":"XX科技"
            }
        ],
        "email":"ry@163.com",
        "id":1,
        "loginDate":1521171180000,
        "loginIp":"127.0.0.1",
        "nickName":"XX科技",
        "password":"$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2",
        "phonenumber":"15888888888",
        "sex":"0",
        "status":"0",
        "userName":"admin"
    },
    {
        "avatar":"",
        "delFlag":"2",
        "depts":[
            {
                "deptName":"XX科技",
                "leader":"XX科技"
            }
        ],
        "email":"22@qq.com",
        "id":9,
        "loginIp":"",
        "nickName":"test",
        "password":"$2a$10$WvdWoE3z9egzAtR2vhSSm.ablDp.I2hU5PzYyXxwWIsBvbR1wfw4O",
        "phonenumber":"18188566545",
        "sex":"0",
        "status":"0",
        "userName":"test"
    },
    {
        "avatar":"",
        "delFlag":"0",
        "depts":[
            {
                "deptName":"XX科技",
                "leader":"XX科技"
            },
            {
                "deptName":"java研发部"
            }
        ],
        "email":"dss@qq.com",
        "id":10,
        "loginIp":"",
        "nickName":"test1",
        "password":"$2a$10$/HqNCw155Wi2l325lnzqg.n6T01D53PAibLPobkL/O33yirtG5zEu",
        "phonenumber":"18188600100",
        "sex":"0",
        "status":"0",
        "userName":"test1"
    }
]
```
