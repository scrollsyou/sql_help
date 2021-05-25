# SQL映射/生成框架/核心功能包
## 项目介绍
> 针对数据库操作核心模块，可单独使用

## 一、开始使用
1. maven引入依赖包
```xml
<dependencies>
    <dependency>
        <groupId>gugusong</groupId>
        <artifactId>sql_help</artifactId>
        <version>0.0.1</version>
    </dependency>
</dependencies>
```
2. 配置数据源  
如果是spring项目，可直接引用数据源对象到config中。
```java
/**
 * 配置datasource
 * @author yousongshu
 *
 */
public class DataSourceFactory {

	/**
	 * 获取测试用的dataSource
	 * @return
	 */
	public static DataSource getDataSource() {
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl("jdbc:mysql://192.168.1.184:3306/sql_help?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8");
		dataSource.setUsername("root");
		dataSource.setPassword("you");
		return dataSource;
	}
}
// 配置获取session会话
private Session getSession() throws SQLException {
		GlogalConfig glogalConfig = new GlogalConfig();
		glogalConfig.setDataSource(DataSourceFactory.getDataSource());
		SessionFactory factory = new SessionFactoryImpl(glogalConfig);
		Session openSession = factory.openSession();
		return openSession;
}
```
当使用spring时，GlogalConfig 可配置到spring中进行管理。  
GlogalConfig参数说明：
| 参数 | 说明 |
| :-----: | :----: |
| dataSource | 配置数据源对象 |
| snowFlake | 雪花随机数对象或可实现其它序列生成类 |
| implicitNamingStrategy | 默认命名策略 |
| columnTypeMapping | 数据库字段类型与java属性类型映射，可扩展实现 |
	
3. 开始使用
```java
  @Test
	public void save() throws Exception {
		Session openSession = getSession();
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setName("aaaa");
		openSession.save(testEntity);
		System.out.println(testEntity.getId() + ":" + testEntity.getName());
	}

	@Test
	public void update() throws Exception {
		Session openSession = getSession();
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setId(4);
		testEntity.setName("bbbb2");
		testEntity.setAa(new Date());
		openSession.update(testEntity);
	}

	@Test
	public void delete() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		SessionTestEntity testEntity = new SessionTestEntity();
		testEntity.setId(5);
		openSession.delete(testEntity);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}
	
	@Test
	public void deleteByExample() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		openSession.delete(ExampleImpl.newInstance().equals("name", "aaaa123").or().condition("length({name}) > ? or length({dd}) < ?", 4, 2).orderByAsc("aa").orderByDesc("dd"), SessionTestEntity.class);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}

	@Test
	public void findAll() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		List ids = new ArrayList<Object>();
		ids.add(1);
		ids.add(2);
		List<SchoolVo> findAll = openSession.findAll(ExampleImpl.newInstance().in("school.id", ids).orderByAsc("student.id").page(), SchoolVo.class);
		System.out.println(findAll);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}

	@Test
	public void findOne() throws Exception {
		Session openSession = getSession();
		SessionTestEntity findOneById = openSession.findOneById(SessionTestEntity.class, 5);
		System.out.println(findOneById);
	}
	
	@Test
	public void findOneVo() throws Exception {
		Session openSession = getSession();
		Example example = ExampleImpl.newInstance().equals("clbum.id", 1);
		SchoolVo findOneById = openSession.findOne(example, SchoolVo.class);
		System.out.println(findOneById);
	}
	
	@Test
	public void findOneById() throws Exception {
		Session openSession = getSession();
		SchoolVo findOneById = openSession.findOneById(SchoolVo.class, 1);
		System.out.println(findOneById);
	}

	@Test
	public void findCount() throws Exception {
		Session openSession = getSession();
		openSession.setAutoCommit(false);
		int findAll = openSession.findCount(ExampleImpl.newInstance().like("name", "%大%"), SchoolVo.class);
		System.out.println(findAll);
		openSession.rollback();
		openSession.setAutoCommit(true);
	}
```
相关bean类：
```java
  @Data
	@ToString
	@Entity(tableName = "test")
	public static class SessionTestEntity{
		@Id(stragegy = GenerationType.IDENTITY)
		private int id;
		
		private String name;
		@Column(sort = 12)
		private Date aa;
		
		private String dd;
	}

@Data
@VOBean(mainPo = School.class, entityAlias = "school")
@Join(entityAlias = "student", po = Student.class, joinConditions = "{student.schoolId} = {id}")
@Join(entityAlias = "clbum", po = Clbum.class, joinConditions = "{id} = {clbum.schoolId}")
public class SchoolVo {

	private Integer id;
	private String name;
	
	@OneToMany(tagerClass = StudentVo.class)
	private List<StudentVo> students;
	@OneToMany(tagerClass = ClbumTestVo.class)
	private Set<ClbumTestVo> clbums;
	
	@Data
	public static class StudentVo{
		@PropertyMapping(originalName = "student.id")
		private Integer id;
		@PropertyMapping(originalName = "student.name")
		private String name;
		@ManyToOne(tagerClass = SchoolTestVo.class)
		private SchoolTestVo school;
	}
	@Data
	public static class SchoolTestVo{
		@PropertyMapping(originalName = "school.name")
		private String name;
	}
	@Data
	public static class ClbumTestVo{
		@PropertyMapping(originalName = "clbum.name")
		private String name;
	}
}
```

## 二、关联查询
### 关联查询设计思路
注：所有bean类都必须存在get/set方法
所有关联查询必须通过vo类进行查询，po类只为数据库中表的映射
查询中使用的所有字段名均为po类中属性名，跟数据库中字段名无关
po类采用@Entity注解指定,如：
```java
@Data
@Entity
public class School {
	
	@Id
	private Integer id;
	private String name;

}
```
vo类采用@VOBean注解指定，查询入口类必须为po/vo类，@VOBean中必须指定主po，如：
```java
@Data
@VOBean(mainPo = School.class, entityAlias = "school")
@Join(entityAlias = "student", po = Student.class, joinConditions = "{student.schoolId} = {id}")
@Join(entityAlias = "clbum", po = Clbum.class, joinConditions = "{id} = {clbum.schoolId}")
public class SchoolVo {

	private Integer id;
	private String name;
	
	@OneToMany(tagerClass = StudentVo.class)
	private List<StudentVo> students;
	@OneToMany(tagerClass = ClbumTestVo.class)
	private Set<ClbumTestVo> clbums;
	
	@Data
	public static class StudentVo{
		@PropertyMapping(originalName = "student.id")
		private Integer id;
		@PropertyMapping(originalName = "student.name")
		private String name;
		@ManyToOne(tagerClass = SchoolTestVo.class)
		private SchoolTestVo school;
	}
	@Data
	public static class SchoolTestVo{
		@PropertyMapping(originalName = "school.name")
		private String name;
	}
	@Data
	public static class ClbumTestVo{
		@PropertyMapping(originalName = "clbum.name")
		private String name;
	}
}
```
相当于sql:
```sql
select school.id school_id,school.name school_name,clbum.id clbum_id,clbum.name clbum_name,clbum.school_id clbum_school_id,student.id student_id,student.name student_name,student.school_id student_school_id 
from school school 
left join clbum clbum on(school.id = clbum.school_id)  
left join student student on(student.school_id = school.id)
```

## 三、Example查询
该查询条件类似于sql中的where条件，样例如下：
```java
ExampleImpl.newInstance().in("student.schoolId", ids).orderByAsc("student.id").page()
```
相当于sql:
```sql
where student.school_id in (?) order by student.id limit ?,?
```
其中“student”,"school"为vo类中关联Po类中所填别名，多表关联时别名不能为空

## 四、分页逻辑
分页逻辑需要通过在Example中调用page()方法进行开启
默认不能Page对象时，分页通过GlogalConfig类中配置的pageHelp
进行获取，当采用拦截器统一处理分页时，可在每一请求到达时
设置pageHelp中的分页对象，代码中需要用到的地方直接调用.page()方法进行分页

样例：
```java
ExampleImpl.newInstance().in("school.id", ids).orderByAsc("student.id").page();
Page page = new Page(1, 10);
ExampleImpl.newInstance().in("school.id", ids).orderByAsc("student.id").page(page);
```
以上两类查询等价于sql :
```sql
1.通过pageHelp中获取page对象，执行limit ?,?
2.调用时输入page对象，执行limit ?,?
```

## 五、分组字段指定/group by
### 使用注解 @GroupBy
该主解只可用于VO类上，其它类不做处理，不生效。

注解使用例子：
```java
@Data
@VOBean(mainPo = School.class, entityAlias = "school")
@Join(entityAlias = "clbum", po = Clbum.class, joinConditions = "{id} = {clbum.schoolId}")
@Join(entityAlias = "student", po = Student.class, joinConditions = "{student.clbumId} = {clbum.id}")
@GroupBy(propertys = {"id", "school.name"})
public class SchoolVo {

	private Integer id;
	private String name;
```
等价于sql:
```sql
group by school.id,school.name
```

## 六、自定义字段映射/支持函数
### 使用注解@FunctionMapping
该注解可用于非PO类中属性进行注解，指定执行该字段的特殊映射关系，
可指定统计函数，一般与@GroupBy注解结合使用，跟@OneToMany
使用可能出现未知数据结构

样例：
```java
@Data
@VOBean(mainPo = School.class, entityAlias = "school")
@Join(entityAlias = "clbum", po = Clbum.class, joinConditions = "{id} = {clbum.schoolId}")
@Join(entityAlias = "student", po = Student.class, joinConditions = "{student.clbumId} = {clbum.id}")
@GroupBy(propertys = {"id", "school.name"})
public class SchoolVo {

	private Integer id;
	private String name;
	
	@FunctionMapping(function = "count({student.id})")
	private Long studentCount;
```
等价于sql:
```sql
select ... count(student.id) school_student_count from ...
```
