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
