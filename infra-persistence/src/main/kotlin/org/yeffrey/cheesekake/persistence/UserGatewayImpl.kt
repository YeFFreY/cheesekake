package org.yeffrey.cheesekake.persistence

import arrow.core.Option
import arrow.core.toOption
import org.mindrot.jbcrypt.BCrypt
import org.yeffrey.cheesekake.domain.users.UserLoginGateway
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.CREDENTIALS

class UserGatewayImpl : UserLoginGateway {
    override fun login(email: String, password: String): Option<Int> = dbQuery { dslContext ->
        val user = dslContext.select(CREDENTIALS.PASSWORD_HASH, CREDENTIALS.USER_ID)
                .from(CREDENTIALS)
                .where(CREDENTIALS.USERNAME.eq(email))
                .fetchOne()
                .toOption()

        user.filter {
            BCrypt.checkpw(password, it[CREDENTIALS.PASSWORD_HASH])
        }.map {
            it[CREDENTIALS.USER_ID]
        }

    }
}