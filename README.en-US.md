# MeetU: A Social Platform for Connecting People

### Purpose of This Project
We are a group of students just beginning to learn programming, and this project is our final-term assignment. The **MeetU** platform allows users to create accounts, make friends, and even search for true love online. Our goal is to make it easier to foster real-life relationships compared to traditional dating apps.

**MeetU** includes the following features:
- **Member creation, update, and deletion, with Google login**
- **Daily random matching (limited to 3 people per day)**
- **Posts and comments**
- **One-on-one and group chat**
- **Follow feature**
- **Host, register, review, and favorite activities**
- **Real-time system notifications to individual or all users**
- **User blocking mechanism**
- **Report feature for users, activities, and comments**

### Technology Stack
- **Backend**: Spring Boot
- **Frontend**: Vue 3
- **Database**: Microsoft SQL Server (MSSQL)
- **Real-time communication**: WebSocket, polling

# For more details, please refer to our MeetU.pptx and demo video
Due to the large file size of the PPT (which includes video), please view it directly at the following link:  
[MeetU Presentation and Demo](https://1drv.ms/p/s!Ark_lrwm9EcYgbFvUlWK7K1gZgVTtw?e=LRsKnx)

### Setup Instructions
To set up and run the project, follow these steps:

1. **Update MSSQL Configuration**:  
   Modify the `application.properties` file with your MSSQL server details, including the port, username, and password.

2. **Update Email Configuration**:  
   In the `application.properties` file, modify the spring.mail configuration for the username and password.  
   To get the password, apply for an application-specific password from Google by navigating to My Account → Security → Two-step verification. If you need further help, search for "application-specific password."

3. **Update google-oauth2.properties Configuration**:  
   Go to the Google Developer Console, create a new project, and then set up the OAuth consent screen and credentials.  
   Once done, find your `client_id`, `client_secret`, and `redirect_uris`, and fill them in the corresponding fields in `google-oauth2.properties`.

4. **Create Database Schema**:  
   Run the `data.sql` script in MSSQL to create the necessary tables and schema.  
   Alternatively, you can use the provided `MeetU.bak` file to set up the default data (note that some images may not display correctly, and it's recommended to update them through the web interface).

5. **Install Dependencies**:  
   In the project's root directory, run the following command to install the necessary packages:  
   ```bash
   npm install

6. **Run the Development Server**:  
   Once the dependencies are installed, start the development server with:
   ```bash
   npm run dev

# Known Issues
Due to time constraints, some features are partially implemented.