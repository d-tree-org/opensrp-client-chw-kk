
[![Build Status](https://travis-ci.org/OpenSRP/opensrp-client-chw.svg?branch=master)](https://travis-ci.org/OpenSRP/opensrp-client-chw) [![Coverage Status](https://coveralls.io/repos/github/OpenSRP/opensrp-client-chw/badge.svg?branch=master)](https://coveralls.io/github/OpenSRP/opensrp-client-chw?branch=master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f68511a1ac164d58a3a48c1926c2326a)](https://www.codacy.com/app/OpenSRP/opensrp-client-chw?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenSRP/opensrp-client-chw&amp;utm_campaign=Badge_Grade)
 
## OpenSRP CHW Client
An open source digital health platform for frontline health workers.

The CHW Client is an OpenSRP application used by Community Health Workers (CHWs), Community Health Assistants (CHAs) and their supervisors to enumerate all households in their catchment area and provide routine child health, antenatal care (ANC), and postnatal care (PNC) services in the community.

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

## Prerequisites
[Tools and Frameworks Setup](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619207/Tools+and+Frameworks+Setup)

## Development setup

### Steps to set up

Clone OpenSRP CHW Client Kizazi Kijacho
```
git clone https://github.com/d-tree-org/opensrp-client-chw-kk.git
```

Clone OpenSRP client chw core module library
```
git clone https://github.com/d-tree-org/opensrp-client-chw-core.git
```

Publish OpenSRP client chw core module library locally in your machine
```
./gradlew uploadArchives -PmavenLocal=true
```

Clone OpenSRP Client Family Library
```
git clone https://github.com/d-tree-org/opensrp-client-family.git
```

Publish OpenSRP Client Family Library locally in your machine
```
./gradlew uploadArchives -PmavenLocal=true
```

Create a github.properties file in a project root directory and add your github username and authentication key
```
gpr.usr=
gpr.key=
```

Add the provided credentials to your `local.properties` file of your OpenSRP CHW Client Kizazi Kijacho app

Enable Google Services Plugin by adding `google-services.json` file into module (app-level) directory of your OpenSRP CHW Client Kizazi Kijacho app

Finally build and run the app from your IDE. Visit [OpenSRP android client app build](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619236/OpenSRP+android+client+app+build) for more information

### Running the tests

[Android client unit tests](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/65570428/OpenSRP+Client)

## Deployment
[Production releases](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/1141866503/How+to+create+a+release+APK)

## Features
-   Child health care
-   Antenatal care (ANC)
-   Postnatal care (PNC) 
-   All Families register

## Versioning
We use SemVer for versioning. For the versions available, see the tags on this repository.
For more details check out <https://semver.org/>

## Authors/Team 
-   The OpenSRP team
-   See the list of contributors who participated in this project from the [Contributors](../../graphs/contributors) link

## Contributing
[Contribution guidelines](https://smartregister.atlassian.net/wiki/spaces/Documentation/pages/6619193/OpenSRP+Developer+s+Guide)

## Documentation
Wiki [OpenSRP Documentation](https://smartregister.atlassian.net/wiki/spaces/Documentation)

## Support
Email: <mailto:techteam@d-tree.org>
Slack workspace: <opensrp.slack.com>

## License
This project is licensed under the Apache 2.0 License - see the LICENSE.md file for details
