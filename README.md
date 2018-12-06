#### This plugin allows you to ban, tempban, mute and warn players.
###### Spigot version: 1.10.2
###### Bungee version: _(doesn't supported yet)_
## Features:
* Customizable messages in messages.yml
* MySQL database support
* All your punishments, that you've issued on the one server, will take effect on all others servers connected to the same database _(will be redesigned in near future by adding bungeecord support)_
* Players cannot override the existing punishment without permission `hfp.[command].override`
* Silent punishments with `-s` flag _(in development)_
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
            command: 'kick'
            if_player_is_online: true
          3:
            command: 'ban'
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

/hfp - `hfp.reload` - reload config and messages.yml

/kick [player] (reason) - `hfp.kick` - kick player

/ban (-f) [player] (reason) - `hfp.ban` - ban player for permanent

/banip (-f) [ip/player] [reason] - `hfp.banip` - ban player's IP for permanent

/tempban (-f) [player] [time] (reason) - `hfp.tempban` - ban player for a specified time _(see **time settings**)_

/banlist - `hfp.banlist` - show all banned players

/banlistip - `hfp.banlist` - show all banned player's IPs

/unban [player] - `hfp.unban` - delete player's ban

/mute (-f) [player] (reason) - `hfp.mute` - mute player for permanent

/tempmute (-f) [player] [time] (reason) - `hfp.tempmute` - mute player for a specified time _(see **time settings**)_

/unmute [player] - `hfp.unmute` - delete player's mute

/warn (-f) [player] (reason) - `hfp.warn` - warn a player

/unwarn (-a) [player] [warn id] - `hfp.unwarn` - delete player's warn whose ID is specified _(or delete all player's warnings if `-a` flag is specified)_

/warnlist [player] - show all player's warnings

/pinfo [player] - `hfp.pinfo` - show information about player (like banned or not, muted or not etc)

`hfp.kick.exempt` - protects player from kick

`hfp.ban.offline` - ban player who in offline

`hfp.ban.override` - override an existing ban

`hfp.ban.force` - allows to use `-f` flag

`hfp.ban.exempt` - protects player from ban

`hfp.mute.offline` - mute player who in offline

`hfp.mute.override` - override an existing mute

`hfp.mute.force` - allows to use `-f` flag

`hfp.ban.exempt` - protects player from kick

`hfp.[command].cooldownBypass` - allows player bypass command cooldown

`hfp.notifyDeniedJoin` - notifies all players with this permission, when player is banned and he's trying to join the server
