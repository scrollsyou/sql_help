## sql-help集成说明

### spring项目中开始使用
> 在spring启动配置中增加@EnableSqlHelp开启框架功能

```
/**
 * 启动程序
 * 
 * @author qinghong
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
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

> 在需要使用到session时进行注入

```
	@Resource
	private Session session;

```

后在方法中直接使用该功能。