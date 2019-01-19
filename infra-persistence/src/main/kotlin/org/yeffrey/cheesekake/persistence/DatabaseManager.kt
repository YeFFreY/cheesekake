package org.yeffrey.cheesekake.persistence

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import javax.sql.DataSource
import kotlin.coroutines.CoroutineContext

object DatabaseManager {
    private lateinit var dispatcher: CoroutineContext
    private lateinit var dslContext: DSLContext


    private fun datasource(connectionUrl: String, poolSize: Int): DataSource {
        val config = HikariConfig()
        config.jdbcUrl = connectionUrl
        config.username = "postgres"
        config.password = "postgres"
        config.maximumPoolSize = poolSize
        return HikariDataSource(config)
    }


    fun initialize(connectionUrl: String, poolSize: Int = 10) {
        //dispatcher = newFixedThreadPoolContext(poolSize, "database-pool")
        dslContext = DSL.using(datasource(connectionUrl, poolSize), SQLDialect.POSTGRES)
    }

    fun <T> dbQuery(block: (dslContext: DSLContext) -> T): T = block(dslContext)

    @Suppress("UNCHECKED_CAST")
    fun <T> dbTransaction(block: (dslContext: DSLContext) -> T): T {
        var returnVal: T? = null
        dslContext.transaction { configuration ->
            val subContext = DSL.using(configuration)
            returnVal = block(subContext)
        }
        return returnVal as T
    }
}
