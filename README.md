## League of legends esports bot

[![Dashboard](https://img.shields.io/static/v1?label=AWS&message=Dashboard&color=green)](https://cloudwatch.amazonaws.com/dashboard.html?dashboard=Predictions-Bot-Dashboard&context=eyJSIjoidXMtZWFzdC0xIiwiRCI6ImN3LWRiLTY1MzUyODg3Mzk1MSIsIlUiOiJ1cy1lYXN0LTFfWWdlV3dsS0tGIiwiQyI6Ijc4OHJ1bGIzdDNvaTc3dTJjbGhoOTlzbGNpIiwiSSI6InVzLWVhc3QtMTo0ODhlOWRmNi1hOThlLTQzMTItOGE0YS0zMzZkYTVkNzI2ZWMiLCJNIjoiUHVibGljIn0=)
[![Discord Chat](https://img.shields.io/discord/802610953396551720?label=support)](https://discord.gg/Dvq8f5KxZT)
[![Discord Bots](https://top.gg/api/widget/status/725169546633281628.svg)](https://top.gg/bot/725169546633281628)



## Bot Usage
This bot stores user data about predictions and various settings the user specifies. By using the features to store your data you consent to have your data stored.
Features such as !predict, !setPasta and !setTimezone will store your data

You can request this data at any time.

## Info
This bot provides schedule, results, and standings for all regions listed in the esports api.
To add this bot to your discord server [Click here](https://discord.com/api/oauth2/authorize?client_id=725169546633281628&permissions=2112&scope=bot)

For support please file a Github issue: https://github.com/mckernant1/lol-predictions-bot/issues/new

### Commands
!info lists this menu

\<league\> refers to one of the league codes (lcs, lpl, lck, ...) 

\<team code\> refers to the 2 or three letter acronym for a team. example: C9 (cloud9), FPX (FunPlus Phoenix)

[number of matches] is optional, picks the number of matches to display. Default is the next or previous days matches

### Esports Commands
`!ongoing` -> Display all the leagues with ongoing tournaments

`!schedule <league> [number of matches]` -> Displays the upcoming games for the region

`!results <league> [number of matches]` -> Displays the most recent results for the region 

`!standings <league>` -> Displays the standings for the region 

`!roster <team code>` -> Displays the teams roster

`!record <league> <team code> [another team code]` -> displays a team's record. If a second team is provided it provides only the record against that team

### Predictions Commands
`!predict <league> [number of matches]` -> Prints a message where you can set your predictions. Disappears after 5 mins.

`!predictions <league> [number of matches]` -> Prints out he predictions for upcoming matches

`!report <league> [number of matches]` -> Reports the most recent matches and who predicted what

`!stats <league> [number of matches]` -> Displays the predictions standings. Default number of matches is the whole split

### User Settings

`!setTimezone <Timezone>` -> !setTimezone <Timezone> -> This will set your timezone. Timezone should be formatted like America/Los_Angeles or a timezone code such as PST or CET. Example: !setTimezone PST

`!setPasta <Pasta>` -> Sets your personal pasta. You can use your servers emotes, but private server emotes that this bot is not in will not work.

`!pasta [number of times]` -> Prints out your pasta that you have saved the specified number of times

### Reminders

`!addReminder <league> <number of hours>` -> Will ping you x amount of hours before the league starts

`!listReminders` -> Will list all of your current reminders

`!deleteReminder <league> <number of hours>` -> Deletes a reminder with the specified league and number of hours beforehand 


## Known Issues

#### User does not have PMs Enabled
the reminders command direct messages a user with a reminder. If the user does not have PMs enable you will see this.
```
ErrorResponseException: 50007: Cannot send messages to this user
```

#### Message is too long
If a server has too many users making predictions there will be too much text to output. Discord has a limit of 2000 characters per message, and some commands will not work.
