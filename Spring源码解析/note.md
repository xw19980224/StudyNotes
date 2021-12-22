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
- prototype 原型 bean 循环依赖（无法解决）org.springframework.beans.factory.support.AbstractBeanFactory#doGetBean --->**isPrototypeCurrentlyInCreation**
- 单例bean通过setXxx或者@Autowired进⾏循环依赖



三级缓存：

- 一级:单例池
- 二级:
- 三级

A在对象实例化完成之后，立马将SpringBean A放入三级缓存（提前暴露自己）

B在创建过程中发现依赖于A，那么去三级缓存使用尚未成型的Bean A。升级放到二级缓存。升级过程中可以进行一些拓展操作。

B创建完成之后会放入一级缓存。

A创建时，就可以使用一级缓存中的Bean B了。
