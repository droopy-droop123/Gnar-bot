package xyz.gnarbot.gnar.commands.executors.mod

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Role
import xyz.gnarbot.gnar.commands.Category
import xyz.gnarbot.gnar.commands.Command
import xyz.gnarbot.gnar.commands.Scope
import xyz.gnarbot.gnar.commands.managed.Executor
import xyz.gnarbot.gnar.commands.managed.ManagedCommand
import xyz.gnarbot.gnar.utils.Context

@Command(
        aliases = arrayOf("autorole"),
        usage = "(set|unset)",
        description = "Set auto-roles that are assigned to users on joining.",
        category = Category.MODERATION,
        scope = Scope.TEXT,
        permissions = arrayOf(Permission.ADMINISTRATOR)
)
class AutoroleCommand : ManagedCommand() {
    @Executor(position = 0, description = "Set the auto-role.")
    fun set(context: Context, role: Role) {
        if (role == context.guild.publicRole) {
            context.send().error("You can't grant the public role!").queue()
            return
        }

        if (!context.guild.selfMember.canInteract(role)) {
            context.send().error("That role is higher than my role! Fix by changing the role hierarchy.").queue()
            return
        }

        context.guildOptions.autoRole = role.id

        context.send().embed("Ignore") {
            description {
                "Users joining the channel will now be granted the role ${role.asMention}."
            }
        }.action().queue()

        context.guildOptions.save()
    }

    @Executor(position = 1, description = "Unset the auto-role.")
    fun unset(context: Context) {
        context.guildOptions.autoRole = null

        context.send().embed("Ignore") {
            description {
                "Unset autorole. Users joining the channel will not be granted any role."
            }
        }.action().queue()

        context.guildOptions.save()
    }

    override fun noMatches(context: Context, args: Array<String>) {
        noMatches(context, args, buildString {
            val role = context.guildOptions.autoRole
            append("Current auto-role: ")
            if (role == null) {
                append("__None__")
            } else {
                append(context.guild.getRoleById(role).asMention)
            }
        })
    }
}