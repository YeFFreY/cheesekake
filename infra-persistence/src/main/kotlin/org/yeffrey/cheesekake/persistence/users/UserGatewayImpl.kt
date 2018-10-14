package org.yeffrey.cheesekake.persistence.users

import arrow.core.Option
import arrow.core.toOption
import org.yeffrey.cheesekake.domain.users.RegisterUserGateway
import org.yeffrey.cheesekake.domain.users.command.Registration
import org.yeffrey.cheesekake.persistence.DatabaseManager.dbQuery
import org.yeffrey.cheesekake.persistence.db.Tables.CREDENTIALS
import org.yeffrey.cheesekake.persistence.db.routines.Encryptit

class UserGatewayImpl: RegisterUserGateway {
    override suspend fun register(registration: Registration): Option<Int> = dbQuery {
        try {
            val encryptedPassword = Encryptit()
            encryptedPassword.setValue(registration.password.value)
            encryptedPassword.execute(it.configuration())
            it.insertInto(CREDENTIALS, CREDENTIALS.USERNAME, CREDENTIALS.PASSWORD_HASH)
                    .values(registration.username.value, encryptedPassword.returnValue)
                    .returning(CREDENTIALS.ID)
                    .fetchOne()[CREDENTIALS.ID].toOption()
        } catch (e: Throwable) {
            println(e)
            Option.empty()
        }
    }
}