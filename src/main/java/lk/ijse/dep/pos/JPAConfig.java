package lk.ijse.dep.pos;


import lk.ijse.dep.pos.dao.CustomerDAO;
import lk.ijse.deppo.crypto.DEPCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@EnableTransactionManagement
@EnableJpaRepositories(basePackageClasses = {CustomerDAO.class})
@PropertySource("classpath:application.properties")
@Configuration
public class JPAConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds, JpaVendorAdapter jpaVendorAdapter){
        LocalContainerEntityManagerFactoryBean lcemf = new LocalContainerEntityManagerFactoryBean();
        lcemf.setDataSource(ds);
        lcemf.setJpaVendorAdapter(jpaVendorAdapter);
        lcemf.setPackagesToScan("lk.ijse.dep.pos.entity");
        return lcemf;
    }

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(env.getProperty("javax.persistence.jdbc.driver"));
        ds.setUrl(env.getProperty("javax.persistence.jdbc.url"));
        ds.setUsername(DEPCrypt.decode(env.getProperty("javax.persistence.jdbc.user"),"dep4"));
        ds.setPassword(DEPCrypt.decode(env.getProperty("javax.persistence.jdbc.password"),"dep4"));
        return ds;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter (){
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);
        jpaVendorAdapter.setDatabasePlatform(env.getProperty("hibernate.dialect"));
        jpaVendorAdapter.setShowSql(env.getProperty("hibernate.show_sql",Boolean.class));
        jpaVendorAdapter.setGenerateDdl(env.getProperty("hibernate.hbm2ddl.auto").equals("update"));
        return jpaVendorAdapter;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf){
        return new JpaTransactionManager(emf);
    }
}
