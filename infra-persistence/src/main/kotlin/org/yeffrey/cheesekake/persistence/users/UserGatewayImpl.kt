package org.yeffrey.cheesekake.persistence.users

import arrow.core.Option
import arrow.core.toOption
import org.jooq.impl.DSL.defaultValue
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.yeffrey.cheesekake.domain.users.LoginUserGateway
import org.yeffrey.cheesekake.domain.users.RegisterUserGateway
import org.yeffrey.cheesekake.domain.users.entities.User
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Tables.CREDENTIALS
import org.yeffrey.cheesekake.persistence.db.Tables.USERS

class UserGatewayImpl : RegisterUserGateway, LoginUserGateway {
    companion object {
        val log: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override suspend fun register(user: User): Option<Int> = dbTransaction {
        try {
            val userId = it.insertInto(USERS, USERS.ID).values(defaultValue(USERS.ID)).returning(USERS.ID).fetchOne()[USERS.ID]
            val encryptedPassword = BCrypt.hashpw(user.credentials.password.value, BCrypt.gensalt())
            it.insertInto(CREDENTIALS, CREDENTIALS.USERNAME, CREDENTIALS.PASSWORD_HASH, CREDENTIALS.USER_ID)
                    .values(user.credentials.username.value, encryptedPassword, userId).execute()
            userId.toOption()
        } catch (e: Throwable) {
            log.error("Unable to register user : ${e.message}")
            Option.empty()
        }
    }

    override suspend fun login(username: String, password: String): Option<Int> = dbTransaction {
        try {
            val user = it.select(CREDENTIALS.PASSWORD_HASH, CREDENTIALS.USER_ID).from(CREDENTIALS).where(CREDENTIALS.USERNAME.eq(username)).fetchOne()
                    ?: return@dbTransaction Option.empty()
            if (BCrypt.checkpw(password, user[CREDENTIALS.PASSWORD_HASH])) Option(user[CREDENTIALS.USER_ID]) else Option.empty()
        } catch (e: Throwable) {
            log.error("Unable to register user : ${e.message}")
            Option.empty()
        }
    }


}