package com.stackroute.keepnote.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.apache.commons.dbcp2.BasicDataSource;



import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.FORMAT_SQL;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;

/*This class will contain the application-context for the application. 
 * Define the following annotations:
 * @Configuration - Annotating a class with the @Configuration indicates that the 
 *                  class can be used by the Spring IoC container as a source of 
 *                  bean definitions
 * @ComponentScan - this annotation is used to search for the Spring components amongst the application
 * @EnableWebMvc - Adding this annotation to an @Configuration class imports the Spring MVC 
 * 				   configuration from WebMvcConfigurationSupport 
 * @EnableTransactionManagement - Enables Spring's annotation-driven transaction management capability.
 *                  
 * 
 * */

@Configuration
@ComponentScan(basePackages= {"com.stackroute.keepnote.*"})
@EnableWebMvc
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class ApplicationContextConfig {

	
	@Autowired
	private Environment env;
	/*
	 * Define the bean for DataSource. In our application, we are using MySQL as the
	 * dataSource. To create the DataSource bean, we need to know: 1. Driver class
	 * name 2. Database URL 3. UserName 4. Password
	 */
	 @Bean (name ="dataSource")
	 @Autowired
	 public BasicDataSource getDataSource() {
		 final BasicDataSource dataSource = new BasicDataSource();
		 dataSource.setDriverClassName("com.mysql.cj.jdbc.driver");
		 dataSource.setUrl("jdbc:mysql://" + System.getenv("MYSQL_HOST") + ":3306/" + System.getenv("MYSQL_DATABASE")+"?verifyServerCertificate=false&useSSL=false&requireSSL=false");
		 dataSource.setUsername(System.getenv("MYSQL_USER"));
		 dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
		 
		 return dataSource;
	 }
	/*
	 * Use this configuration while submitting solution in hobbes.
	 * dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
	 * dataSource.setUrl("jdbc:mysql://" + System.getenv("MYSQL_HOST") + ":3306/" +
	 * System.getenv("MYSQL_DATABASE")
	 * +"?verifyServerCertificate=false&useSSL=false&requireSSL=false");
	 * dataSource.setUsername(System.getenv("MYSQL_USER"));
	 * dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
	 */
	 private Properties hibernateProperties() {
		 final Properties properties = new Properties();
		 properties.setProperty(HBM2DDL_AUTO, env.getProperty("hibernate.hbm2ddl.auto"));
		 properties.setProperty(DIALECT, env.getProperty("hibernate.dialect"));
		 properties.setProperty(SHOW_SQL, env.getProperty("hibernate.show_sql"));
		 properties.setProperty(FORMAT_SQL, env.getProperty("hibernate.format_sql"));
		 return properties;
	 }

	/*
	 * create a getter for Hibernate properties here we have to mention 1. show_sql
	 * 2. Dialect 3. hbm2ddl
	 */

	/*
	 * Define the bean for SessionFactory. Hibernate SessionFactory is the factory
	 * class through which we get sessions and perform database operations.
	 */
	 @Bean(name = "sessionFactory")
	 @Autowired
	 public LocalSessionFactoryBean getSessionFactory(final DataSource dataSource) {
		 final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		 sessionFactory.setDataSource(dataSource);
		 sessionFactory.setPackagesToScan("com.stackroute.keepnote.*");
		 sessionFactory.setHibernateProperties(hibernateProperties());
		 return sessionFactory;
	 }
	/*
	 * Define the bean for Transaction Manager. HibernateTransactionManager handles
	 * transaction in Spring. The application that uses single hibernate session
	 * factory for database transaction has good choice to use
	 * HibernateTransactionManager. HibernateTransactionManager can work with plain
	 * JDBC too. HibernateTransactionManager allows bulk update and bulk insert and
	 * ensures data integrity.
	 */
	 
	 @Bean
	 public HibernateTransactionManager getTransactionManager() {
		 HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		 transactionManager.setSessionFactory(getSessionFactory(getDataSource()).getObject());
		 return transactionManager;
	 }

}
