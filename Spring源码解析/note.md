# Spring源码解析

FactoryBean和BeanFactory

​	BeanFactory接口是容器的顶级接口，定义了容器的一些基础行为，负责生产和管理Bean的一个工厂，具体似乎用它下面的子接口类型。

​	Spring中Bean有两种，一种是普通Bean，一种是工厂Bean（Factory Bean），Factory Bean可以生产某一个类型的Bean实例（返回给我们），也就是说我们可以借助于它自定义Bean的创建过程。

​	Bean创建的三种方式中静态方法和实例化方法

~~~
// 可以让我们自定义Bean的创建过程（完成复杂bean的定义）
public interface FactoryBean<T> {

	@Nullable
	// 返回FactoryBean创建的Bean实例，如果isSingleton返回true，则该实例会放到Spring容器的单例缓存池中Map
	T getObject() throws Exception;


	@Nullable
	// 返回FactoryBean创建的Bean类型
	Class<?> getObjectType();

	// 返回作用域是否单例
	default boolean isSingleton() {
		return true;
	}

}

~~~

后置处理器

Spring提供了两种后置处理Bean的拓展接口，分别为 BeanPostProcessor 和 BeanFactoryPostProcessor ，两者在使用上是有所区别的。

工厂初始化（BeanFactory） --->  Bean对象

在 BeanFactory 初始化之后可以使用 BeanFactoryPostProcessor 进行后置处理一些事情。

在 Bean 对象实例化（并不是 Bean 的整个生命周期完成）之后可以使用 BeanPostProcessor 进行后置处理做一些事情。



SpringBean的生命周期

### BeanDefinition加载解析及注册子流程

入口：org.springframework.context.support.**AbstractApplicationContext**#**obtainFreshBeanFactory**

真正执行BeanDefinition加载：

org.springframework.context.support.**AbstractRefreshableApplicationContext**#**loadBeanDefinitions**

- org.springframework.context.support.**AbstractXmlApplicationContext**#**loadBeanDefinitions(DefaultListableBeanFactory)**

- org.springframework.context.support.**AbstractXmlApplicationContext**#**loadBeanDefinitions(XmlBeanDefinitionReader)**

  - org.springframework.beans.factory.support.**AbstractBeanDefinitionReader**#**loadBeanDefinitions(String ...)**

  - org.springframework.beans.factory.support.**AbstractBeanDefinitionReader**#**loadBeanDefinitions(String)**

  - org.springframework.beans.factory.support.**AbstractBeanDefinitionReader**#**loadBeanDefinitions**( **String**, **Set<Resource>**)

  - org.springframework.beans.factory.support.**AbstractBeanDefinitionReader**#**loadBeanDefinitions**(**Resource**...)

    - org.springframework.beans.factory.xml.**XmlBeanDefinitionReader**#**loadBeanDefinitions**(Resource)

    - org.springframework.beans.factory.xml.**XmlBeanDefinitionReader**#**loadBeanDefinitions**(**EncodedResource**)

    - org.springframework.beans.factory.xml.**XmlBeanDefinitionReader**#**doLoadBeanDefinitions**

    - 读取xml为document对象完成

    - org.springframework.beans.factory.xml.**XmlBeanDefinitionReader**#**registerBeanDefinitions**  (开始注册)

      - org.springframework.beans.factory.xml.**DefaultBeanDefinitionDocumentReader**#**registerBeanDefinitions**

      - org.springframework.beans.factory.xml.**DefaultBeanDefinitionDocumentReader**#**doRegisterBeanDefinitions**

      - org.springframework.beans.factory.xml.**DefaultBeanDefinitionDocumentReader**#**parseBeanDefinitions**

      - org.springframework.beans.factory.xml.**DefaultBeanDefinitionDocumentReader**#**parseDefaultElement**

      - org.springframework.beans.factory.xml.**DefaultBeanDefinitionDocumentReader**#**processBeanDefinition**

        - org.springframework.beans.factory.support.**BeanDefinitionReaderUtils**#**registerBeanDefinition**

          - org.springframework.beans.factory.support.**DefaultListableBeanFactory**#**registerBeanDefinition**

          

### Bean加载流程

入口org.springframework.context.support.**AbstractApplicationContext**#**finishBeanFactoryInitialization**

- org.springframework.context.support.**AbstractApplicationContext**#**finishBeanFactoryInitialization**
  - org.springframework.beans.factory.support.**DefaultListableBeanFactory**#**preInstantiateSingletons**
    - org.springframework.beans.factory.support.**AbstractBeanFactory**#**getBean**(**String**)
    - org.springframework.beans.factory.support.**AbstractBeanFactory**#**doGetBean**
      - org.springframework.beans.factory.support.**DefaultSingletonBeanRegistry**#**getSingleton**(String, ObjectFactory<?>)



### Spring循环依赖问题：

#### 什么是循环依赖？

​	循环依赖其实就是循环引⽤，也就是两个或者两个以上的 Bean 互相持有对⽅，最终形成闭环。⽐如A

依赖于B，B依赖于C，C⼜依赖于A。

![](C:\workspase\StudyNotes\Spring源码解析\循环依赖.png)

Spring中循环依赖场景有：

- 构造器的循环依赖（构造器注入）：无法解决，只能抛出 BeanCurrentlyCreationException 异常
- Field属性的循环依赖（Set注入）

在解决属性循环依赖时，Spring采用的是提前暴露对象的方法。



循环依赖的处理机制：

- 单例 bean 构造器参数循环依赖（无法解决）：构造器在初始化时需要赋值，而参数并未进行初始化。
- prototype 原型 bean 循环依赖（无法解决）org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean ---> **isPrototypeCurrentlyInCreation**
- 单例bean通过setXxx或者@Autowired进⾏循环依赖



三级缓存：

- 一级:单例池
- 二级:
- 三级

A在对象实例化完成之后，立马将SpringBean A放入三级缓存（提前暴露自己）

B在创建过程中发现依赖于A，那么去三级缓存使用尚未成型的Bean A。升级放到二级缓存。升级过程中可以进行一些拓展操作。

B创建完成之后会放入一级缓存。

A创建时，就可以使用一级缓存中的Bean B了。





## 第六部分 Spring AOP应用

Aop本质：在不改变原有的业务逻辑的基础上，增加横切逻辑。横切逻辑代码往往是权限校验代码、日志记录代码、事务控制代码、性能监控代码。



### 第5节 Spring 声明式事务的⽀持

**编程式事务**：在业务代码中添加事务控制代码，这样的事务控制机制就叫做编程式事务 

**声明式事务**：通过xml或者注解配置的⽅式达到事务控制的⽬的，叫做声明式事务



### 5.1 事务回顾

#### 5.1.1 事务的概念

#### 5.1.2 事务的四⼤特性

**原⼦性（Atomicity）** 原⼦性是指事务是⼀个不可分割的⼯作单位，事务中的操作要么都发⽣，要么都 不发⽣。

**⼀致性（Consistency）** 事务必须使数据库从⼀个⼀致性状态变换到另外⼀个⼀致性状态。

**隔离性（Isolation）** 事务的隔离性是多个⽤户并发访问数据库时，数据库为每⼀个⽤户开启的事务， 每个事务不能被其他事务的操作数据所⼲扰，多个并发事务之间要相互隔离。

**持久性（Durability）**

持久性是指⼀个事务⼀旦被提交，它对数据库中数据的改变就是永久性的，接下来即使数据库发⽣故障 也不应该对其有任何影响。



#### 5.1.3 事务的隔离级别

- 脏读
- 不可重复读
- 虚读（幻读）



#### 5.1.4 事务的传播⾏为

事务往往在service层进⾏控制，如果出现service层⽅法A调⽤了另外⼀个service层⽅法B，A和B⽅法本 身都已经被添加了事务控制，那么A调⽤B的时候，就需要进⾏事务的⼀些协商，这就叫做事务的传播⾏ 为。

 A调⽤B，我们站在B的⻆度来观察来定义事务的传播⾏为

| PROPAGATION_REQUIRED     | 如果当前没有事务，就新建⼀个事务，如果已经存在⼀个事务中， 加⼊到这个事务中。这是最常⻅的选择。 |
| ------------------------ | ------------------------------------------------------------ |
| **PROPAGATION_SUPPORTS** | **⽀持当前事务，如果当前没有事务，就以⾮事务⽅式执⾏。**     |

数据库共定义了四种隔离级别

- **Serializable（串⾏化）**：可避免脏读、不可重复读、虚读情况的发⽣。
- **Repeatable read（可重复读）**：可避免脏读、不可重复读情况的发⽣。(幻读有可能发⽣)
- **Read committed（读已提交）**：可避免脏读情况发⽣。不可重复读和幻读⼀定会发⽣。
- **Read uncommitted（读未提交）**：最低级别，以上情况均⽆法保证。

### 5.2 Spring中事务的API

~~~java
public interface PlatformTransactionManager extends TransactionManager {

	// 获取事务状态信息
	TransactionStatus getTransaction(@Nullable TransactionDefinition definition)
			throws TransactionException;

	// 提交事务
	void commit(TransactionStatus status) throws TransactionException;

	// 回滚事务
	void rollback(TransactionStatus status) throws TransactionException;

}
~~~

**作用**

此接口是Spring的事务管理器核心接口。Spring本身并不支持事务实现，只是负责提供标准，应用底层支持什么样的事务，需要提供具体实现类。



jdbcTemplate、Mybatis ----》DataSourceTransactionManager

Hibernate ----》HibernateTransactionManager



DataSourceTransactionManager 归根结底是横切逻辑代码，声明式事务要做的就是使⽤Aop（动态代 理）来将事务控制逻辑织⼊到业务代码。



### 5.3 Spring 声明式事务配置

- 纯xml模式

xml配置

~~~xml
<tx:advice id="txAdvice" transaction-manager="transactionManager">
 <!--定制事务细节，传播⾏为、隔离级别等-->
 <tx:attributes>
 <!--⼀般性配置-->
 <tx:method name="*" read-only="false"
propagation="REQUIRED" isolation="DEFAULT" timeout="-1"/>
 <!--针对查询的覆盖性配置-->
 <tx:method name="query*" read-only="true"
propagation="SUPPORTS"/>
 </tx:attributes>
 </tx:advice>
 <aop:config>
 <!--advice-ref指向增强=横切逻辑+⽅位-->
 <aop:advisor advice-ref="txAdvice" pointcut="execution(*
com.lagou.edu.service.impl.TransferServiceImpl.*(..))"/>
 </aop:config>
~~~

- 基于XML+注解

xml配置

~~~xml
<!--配置事务管理器-->
<bean id="transactionManager"
class="org.springframework.jdbc.datasource.DataSourceTransactionManage
r">
 <property name="dataSource" ref="dataSource"></property>
</bean>
<!--开启spring对注解事务的⽀持-->
<tx:annotation-driven transaction-manager="transactionManager"/>
~~~

在接⼝、类或者⽅法上添加@Transactional注解

~~~java
@Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
~~~



- 基于纯注解

Spring基于注解驱动开发的事务控制配置，只需要把 xml 配置部分改为注解实现。只是需要⼀个 注解替换掉xml配置⽂件中的 `<tx:annotation-driven transaction-manager="transactionManager"/>` 配置。 在 Spring 的配置类上添加 @EnableTransactionManagement 注解即可

~~~java
@EnableTransactionManagement//开启spring注解事务的⽀持
public class SpringConfiguration {
}
~~~



