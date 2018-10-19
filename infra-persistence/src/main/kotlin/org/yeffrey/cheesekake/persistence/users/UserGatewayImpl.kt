package org.yeffrey.cheesekake.persistence.users

import arrow.core.Option
import arrow.core.toOption
import org.jooq.impl.DSL.defaultValue
import org.yeffrey.cheesekake.domain.users.RegisterUserGateway
import org.yeffrey.cheesekake.domain.users.entities.User
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbTransaction
import org.yeffrey.cheesekake.persistence.db.Tables.CREDENTIALS
import org.yeffrey.cheesekake.persistence.db.Tables.USERS
import org.yeffrey.cheesekake.persistence.db.routines.Encryptit

class UserGatewayImpl: RegisterUserGateway {
    override suspend fun register(user: User): Option<Int> = dbTransaction {
        val userId = it.insertInto(USERS, USERS.ID).values(defaultValue(USERS.ID)).returning(USERS.ID).fetchOne()[CREDENTIALS.ID]
        val encryptedPassword = Encryptit()
        encryptedPassword.setValue(user.credentials.password.value)
        encryptedPassword.execute(it.configuration())
         it.insertInto(CREDENTIALS, CREDENTIALS.USERNAME, CREDENTIALS.PASSWORD_HASH, CREDENTIALS.USER_ID)
                .values(user.credentials.username.value, encryptedPassword.returnValue, userId).execute()
        userId.toOption()
    }
}