## League of legends esports bot
Semaphore Pipeline [![Build Status](https://mckernant1.semaphoreci.com/badges/lol-predictions-bot/branches/master.svg?style=shields)](https://mckernant1.semaphoreci.com/projects/lol-predictions-bot)

This bot provides schedule, results, and standings for all regions listed in the esports api.
To add this bot to your discord server [Click here](https://discord.com/api/oauth2/authorize?client_id=725169546633281628&permissions=2112&scope=bot)

For support please file a Github issue: https://github.com/mckernant1/lol-predictions-bot/issues/new

### Commands
!info lists this menu

\<league\> refers to one of the league codes (lcs, lpl, lck, ...) 

[number of matches] is optional, picks the number of matches to display. Default is the next or previous days matches

### Esports Commands
`!schedule <league> [number of matches]` -> Displays the upcoming games for the region

`!results <league> [number of matches]` -> Displays the most recent results for the region 

`!standings <league>` -> Disaplys the standings for the region 

### Predictions Commands
`!predict <league> [number of matches]` -> Prints a message where you can set your predictions. Disappears after 5 mins.

`!predictions <league> [number of matches]` -> Prints out he predictions for upcoming matches

`!report <league> [number of matches]` -> Reports the most recent matches and who predicted what

`!stats <league> [number of matches]` -> Displays the predictions standings. Default number of matches is the whole split
