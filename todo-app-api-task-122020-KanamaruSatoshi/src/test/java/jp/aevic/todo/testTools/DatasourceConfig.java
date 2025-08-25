package jp.aevic.todo.testTools;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * DataSourceの設定に関するクラス
 */
@Configuration
public class DatasourceConfig {
    /**
     * 空文字のセットアップををxmlで許容する設定
     *
     * @return 設定が反映されたBean
     */
    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean bean = new DatabaseConfigBean();

        bean.setAllowEmptyFields(true);

        return bean;
    }

    /**
     * Spring側にnullカラム許容の設定を行うメソッド
     *
     * @param dbUnitDatabaseConfig テストで使用するDbunitの設定
     * @param dataSource           dataSource
     * @return springに設定を反映させるBean
     */
    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(
            DatabaseConfigBean dbUnitDatabaseConfig,
            DataSource dataSource
    ) {
        DatabaseDataSourceConnectionFactoryBean bean =
                new DatabaseDataSourceConnectionFactoryBean(dataSource);
        bean.setDatabaseConfig(dbUnitDatabaseConfig);
        return bean;
    }
}

