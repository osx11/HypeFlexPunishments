#### This plugin allows you to ban, tempban, mute and warn players.
#### This plugin has full UUID support so you can easily use it in offline server mode. (plugin stores players' UUIDs in database)
#### Moreover this plugin has many features that standart minecraft or Essentials plugin don't have.
###### Spigot version: 1.10.2
###### Bungee version: _(doesn't supported yet)_
## Features:
* Customizable messages in messages.yml
* MySQL database support
* All your punishments, that you've issued on the one server, will take effect on all others servers connected to the same database _(will be redesigned in near future by adding bungeecord support)_
* Players cannot override the existing punishment without permission `hfp.[command].override`
* Flags for commands such as '-s' (silent execute) or '-f' (force execute)
* Support offline mode
* Web interface _(in development)_*


## Configuration:

      MySQL:
        login: login
        password: password
        url: jdbc:mysql://host:port/database

      warns:
        command_auto_execute: true
        delete_all_warnings_after_last_warn: true
        on_warn_count:
          2:
            command: 'kick %player% for count of warns: %warnsCount%"'
            if_player_is_online: true
          3:
            command: 'ban %player% for count of warns: %warnsCount%"'
            if_player_is_online: false

      cooldowns:
        kick: 30
        ban: 60
        banip: 60
        tempban: 120
        mute: 180
        muteip: 190
        tempmute: 200
        warn: 220
        
`command_auto_execute`: specified commands in `on_warn_count` will be executed by console, when player's warnings count will be equal to specified count in `on_warn_count`

`delete_all_warnings_after_last_warn`: all player's warnings will be deleted from database, when it's count will be equal the last element in `on_warn_count`

`cooldowns`: cooldowns for commands in seconds (will have effect only for commands provided by HFP plugin)

## Commands & Permissions:
###### []: required, (): optional
###### `-f` - force command execute (plugin won't check if player is online)
###### `-s` - silent command execute (plugin won't broadcast the message)

/hfp - `hfp.reload` - reload config and messages.yml


### Kicks

/kick (-s) (-f) [player] (reason) - `hfp.kick` - kick player
`hfp.kick.force` - use `-f` flag
`hfp.kick.silent` - use `-s` flag
`hfp.kick.exempt` - immunity to this type of punishment
`hfp.kick.notify` - notify all online players when player has been punished


### Bans

/ban (-f) (-s) [player] (reason) - `hfp.ban` - ban player for permanent
/tempban (-f) (-s) [player] [time] (reason) - `hfp.tempban` - ban player for a specified time
`hfp.ban.force` - use `-f` flag
`hfp.ban.silent` - use `-s` flag
`hfp.ban.exempt` - immunity to this type of punishment
`hfp.ban.notify` - notify all online players when player has been punished
`hfp.ban.override` - override the existing ban
`hfp.ban.offline` - ban player in offline

/banip (-f) (-s) [ip/player] [reason] - `hfp.banip` - ban player's IP for permanent
`hfp.banip.force` - use `-f` flag
`hfp.banip.silent` - use `-s` flag
`hfp.banip.exempt` - immunity to this type of punishment
`hfp.banip.notify` - notify all online players when player has been punished
`hfp.banip.override` - override the existing ban

/banlist - `hfp.banlist` - show all banned players
/banlistip - `hfp.banlist` - show all banned player's IPs

/unban (-s) [player] - `hfp.unban` - delete player's ban
`hfp.unban.silent` - use `-s` flag
`hfp.unban.notify` - notify all online players when player has been unbanned

/unbanip (-s) [ip/player] - `hfp.unbanip` - unbans player's ip address
`hfp.unban.silent` - use `-s` flag
`hfp.unban.notify` - notify all online players when player has been unbanned


### Mutes

/mute (-f) (-s) [player] (reason) - `hfp.mute` - mute player for permanent
`hfp.mute.force` - use `-f` flag
`hfp.mute.silent` - use `-s` flag
`hfp.mute.exempt` - immunity to this type of punishment
`hfp.mute.notify` - notify all online players when player has been punished
`hfp.mute.override` - override the existing mute
`hfp.mute.offline` - mute player in offline

/tempmute (-f) (-s) [player] [time] (reason) - `hfp.tempmute` - mute player for a specified time
`hfp.mute.force` - use `-f` flag
`hfp.mute.silent` - use `-s` flag
`hfp.mute.exempt` - immunity to this type of punishment
`hfp.mute.notify` - notify all online players when player has been punished
`hfp.mute.override` - override the existing mute
`hfp.mute.offline` - mute player in offline

/muteip (-f) (-s) [ip/player] [reason] - `hfp.muteip` - mute player's IP for permanent
`hfp.muteip.force` - use `-f` flag
`hfp.muteip.silent` - use `-s` flag
`hfp.muteip.exempt` - immunity to this type of punishment
`hfp.muteip.notify` - notify all online players when player has been punished
`hfp.muteip.override` - override the existing mute

/unmute (-s) [player] - `hfp.unmute` - delete player's ban
`hfp.unmute.silent` - use `-s` flag
`hfp.unmute.notify` - notify all online players when player has been unmuted

/unmuteip (-s) [ip/player] - `hfp.unmuteip` - unmute player's ip address
`hfp.unmuteip.silent` - use `-s` flag
`hfp.unmuteip.notify` - notify all online players when player has been unmuted

### Warnings

/warn (-f) (-s) [player] (reason) - `hfp.warn` - warn a player
`hfp.warn.force` - use `-f` flag
`hfp.warn.silent` - use `-s` flag
`hfp.warn.exempt` - immunity to this type of punishment
`hfp.warn.notify` - notify all online players when player has been warned
`hfp.warn.offline` - warn player in offline

/unwarn (-s) [player] [warn id] - `hfp.unwarn` - delete player's warn with specified id
/unwarn (-a) (-s) [player] - `hfp.unwarn` - delete all player's warnings
`hfp.unwarn.silent` - use `-s` flag
`hfp.unwarn.all` - use `-a` flag

/warnlist [player] `hfp.warnlist` - show all player's warnings with it's id


### Common

/pinfo [player] - `hfp.pinfo` - show information about player (like banned or not, muted or not etc)
`hfp.[command].cooldownBypass` - allows player bypass command cooldown
`hfp.notifyDeniedJoin` - notifies all players with this permission when player is banned and he's trying to join the server

