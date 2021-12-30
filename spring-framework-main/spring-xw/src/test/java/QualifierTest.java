import com.lagou.edu.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * @ClassName: QualifierTest
 * @Author: MaxWell
 * @Description:
 * @Date: 2021/12/23 10:32
 * @Version: 1.0
 */
public class QualifierTest {

	@Test
	public void demo(){
		AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
		applicationContext.register(QualifierTest.class);
		applicationContext.register();

		User bean = applicationContext.getBean(User.class);
		System.out.println(bean);

		applicationContext.close();
	}

	@Autowired
	private User user;

	@Bean
	public User user(){
		return createUser("user1");
	}

	private static User createUser(String name){
		User user = new User();
		user.setName(name);
		return user;
	}

	@Override
	public String toString() {
		return "QualifierTest{" +
				"user=" + user +
				'}';
	}
}
