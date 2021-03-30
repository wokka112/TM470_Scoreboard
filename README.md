
ScoreBoard is an Android app for tracking competitive board game scores and statistics.
Users can store details on their board game group, members and games played, and from this
have monthly scores and overall skill ratings calculated. They can then see and compare
these stats across the group to see who is most skilled at what category of game, or
who has won the most difficult games this month and has the highest score overall.

A modified Elo's skill rating system is used to calculate rough skill ratings for games
with more than 2 teams of players.

All details are stored locally on a user's phone.

# ScoreBoard

ScoreBoard is an Android app for board gaming groups. ScoreBoard can be used to record group members and the games played and their outcomes.
From these games played competitive statistics in the form of monthly scores and overall skill ratings are produced which can then be viewed
and compared. The goal of ScoreBoard is to provide a free app that can be used to keep a record of games played in gaming groups and promote
an environment of healthy, friendly competitive for those groups that want such an environment. This was developed as a final year university
project and, while the application works and provides the desired outcomes, there are currently no plans to release it on the app store.

## Getting Started

Clone the repository: https://github.com/wokka112/TM470_Scoreboard.
Use any IDE of your choice to start viewing and editing files. Android Studio is a free IDE for Android development produced by Google.

### Prerequisites

None.

## Deployment

Currently the best way to deploy the app to an Android device is to connect the device to a computer and Run the application via an IDE or
command line on said device. This will install the application. Before doing this make sure to go into the source code, go to the AppDatabase 
class located in the source files at app/src/main/java/com/floatingpanda/scoreboard/data and comment out the lines 107 - 943. These lines 
are test data that fill the app with example groups, members and games played. If you don't comment this out, the app will start with a set 
of random groups, members and games.

## Contributing

No contributions are currently accepted for this project. Feel free to clone it and do as you like under the MIT license though.

## Authors

* **Adam Poole** - Everything - [wokka112](https://github.com/wokka112)

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* [Rankade](https://rankade.com/) gave a lot of inspiration. I also highly recommend checking them out for the more competitive as they provide a much more accurate skill rating system.
