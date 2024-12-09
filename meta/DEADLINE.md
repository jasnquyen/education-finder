# Deadline

Modify this file to satisfy a submission requirement related to the project
deadline. Please keep this file organized using Markdown. If you click on
this file in your GitHub repository website, then you will see that the
Markdown is transformed into nice-looking HTML.

## Part 1.1: App Description

    I've created a city-based university finder which takes a user-input city and returns a list of universities within the area.
    With this information, a map, and school-based data will be returned.
    - View detailed information such as location, costs, enrollment, etc.
    - View selected colleges and see surrounding locations

    The APIs that were used were Google Maps API and College Scorecard API. These two API's work together as
    College Scorecard returns colleges based off city and displays school-based data, while Google Maps API generates a map off the address of
    the selected school, and generates information based off it.

    GitHub Repository: [https://github.com/jan19557/cs1302-api-app/tree/main](https://github.com/jan19557/cs1302-api-app/tree/main)

TODO WRITE / REPLACE

## Part 1.2: APIs

> For each RESTful JSON API that your app uses (at least two are required),
> include an example URL for a typical request made by your app. If you
> need to include additional notes (e.g., regarding API keys or rate
> limits), then you can do that below the URL/URI. Placeholders for this
> information are provided below. If your app uses more than two RESTful
> JSON APIs, then include them with similar formatting.

### API 1

```
https://api.data.gov/ed/collegescorecard/v1/schools?api_key=YOUR_API_KEY&school.name=Harvard+University&school.city=Atlanta&fields=school.name,school.city,school.address,latest.admissions.admission_rate.overall,latest.cost.attendance.academic_year,latest.student.size

```

> College Scoreboard API: requires API KEY and has a free tier offering 1000 rate limit. Query fetches data about a specific school, including its name, city, address, admission rate, annual cost, and enrollment size.

### API 2

```
https://maps.googleapis.com/maps/api/place/findplacefromtext/json?input=Harvard+University&inputtype=textquery&fields=name,rating,formatted_address&key=YOUR_API_KEY

```

```
https://maps.googleapis.com/maps/api/staticmap?center=33.7756,-84.3963&zoom=15&size=600x300&markers=color:red|label:S|33.7756,-84.3963&key=YOUR_API_KEY

```

> Google Maps API: requires API KEY and has free tier offering 1000 rate limit. First query specifically returns location-based data including address and ratings. Second query is used to generate a static map image of the location, based off coordinates.

## Part 2: New

Something I learned from this project is how to enforce collaboration on two APIs to broaden the capabilities of a program. This included being able to handle errors caused by information from the different APIs interacting and learning how to create bridges. Alongside this, learning how to use APIs was the biggest takeaway. Having to navigate the usage of API keys and query format was very helpful and informative. While it is not always possible to make all data work, learning how to adapt to this was huge.

## Part 3: Retrospect

If I could go back to the beginning of the project, I would've used College Scoreboard API off the bat. Originally I used the DataUSA API which proved to be more inefficient due to its inability to collaborate with the Google Maps API as well due to naming differences. The use of College Scoreboard API and Google Maps API was a lot more efficient. Alongside this, I would've better organized my classes so that the project would be a lot cleaner.
