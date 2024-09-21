create database MeetU;
use MeetU;

create table users(
	userId int primary key not null identity(1,1),
	userMail varchar(50) unique not null,
	userPwd varchar(MAX) not null,
	vip char(1) default 0 not null,
	userIsBan char(1) default 'N' not null,
	deleteState bit default 0 not null
);
create table users_profile(
	userId int primary key not null,
	userName nvarchar(50) not null,
	userGender char(1) not null,
	userPics varchar(MAX),
	userBirth date not null,
	userLocation nvarchar(50) not null,
	userJob nvarchar(50),
	userJobPosi nvarchar(50),
	userIntroduction nvarchar(MAX),
	userPreferAct char(1) not null,
	userPreferGen char(1) default 'N' not null,
	userFind char(1) default 'N' not null,
	userPreferAgeMax int not null,
	userPreferAgeMin int not null,
	userHobby nvarchar(100),
	foreign key(userId) references users(userId)
);
CREATE TABLE activities_tag (
    activitiesTagId INT IDENTITY(1,1) PRIMARY KEY,
    activitiesTag VARCHAR(50) NOT NULL UNIQUE
);
CREATE TABLE activities (
    activitiesId INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    activitiesReportStatus CHAR(1) DEFAULT 'N' NOT NULL,
    activitiesType CHAR(1) NOT NULL,
    hostId INT FOREIGN KEY REFERENCES users(userId),
    activitiesPics VARBINARY(MAX),
    activitiesTitle NVARCHAR(50) NOT NULL,
    activitiesStartDate DATETIME NOT NULL,
    activitiesEndDate DATETIME NOT NULL,
    activitiesLocation NVARCHAR(50) NOT NULL,
    activitiesVerifyDate DATETIME NOT NULL,
    activitiesContent NVARCHAR(200) NOT NULL,
    activitiesSharing CHAR(1) NOT NULL,
    activitiesAmt INT DEFAULT 0 NOT NULL,
    activitiesMaxPeo INT NOT NULL,
    activitiesTagId INT FOREIGN KEY REFERENCES activities_tag(activitiesTagId)
);

CREATE TABLE activities_comment (
    activitiesCommentId INT IDENTITY(1,1) PRIMARY KEY NOT NULL,
    activitiesReportStatus CHAR(1) DEFAULT 'N' NOT NULL,
    fk_activitiesId INT FOREIGN KEY REFERENCES activities(activitiesId),
    fk_userId INT FOREIGN KEY REFERENCES users(userId),
    messageContent NVARCHAR(50) NOT NULL,
    messageTime DATETIME DEFAULT GETDATE()
);

CREATE TABLE activities_collect (
    collectUserId INT NOT NULL,
    collectActId INT NOT NULL,
    PRIMARY KEY (collectUserId, collectActId),
    FOREIGN KEY (collectUserId) REFERENCES users(userId),
    FOREIGN KEY (collectActId) REFERENCES activities(activitiesId)
);

CREATE TABLE post (
    postId INT IDENTITY(1,1) PRIMARY KEY,
    postUserId INT NOT NULL,
    caption NVARCHAR(MAX),
    imageUrl VARCHAR(255),
    videoUrl VARCHAR(255),
    postCreatedAt DATETIME DEFAULT GETDATE(),
    postUpdatedAt DATETIME DEFAULT GETDATE(),
    postReportStatus CHAR(1) DEFAULT 'N' NOT NULL,
    postType VARCHAR(255) DEFAULT 'POST' NOT NULL, 
    CONSTRAINT FK_PostUser FOREIGN KEY (postUserId) REFERENCES Users(userId)
);

CREATE TABLE post_comment (
    commentId INT IDENTITY(1,1) PRIMARY KEY,
    postId INT NOT NULL,
    commentUserId INT NOT NULL,
    commentContent NVARCHAR(MAX),
    commentCreatedAt DATETIME DEFAULT GETDATE(),
    commentUpdatedAt DATETIME DEFAULT GETDATE(),
    isEdited BIT DEFAULT 0,
    parentCommentId INT,
    commentReportStatus CHAR(1) DEFAULT 'N' NOT NULL,
    commentType VARCHAR(255) DEFAULT 'COMMENT' NOT NULL, 
    replyCount INT DEFAULT 0 NOT NULL,
    CONSTRAINT FK_Post FOREIGN KEY (postId) REFERENCES post(postId),
    CONSTRAINT FK_CommentUser FOREIGN KEY (commentUserId) REFERENCES Users(userId),
    CONSTRAINT FK_ParentComment FOREIGN KEY (parentCommentId) REFERENCES post_comment(commentId)
);


CREATE TABLE post_like (
    likeUserId INT NOT NULL,
    targetType VARCHAR(50) NOT NULL CHECK (targetType IN ('post', 'comment')),
    targetId INT NOT NULL,
    PRIMARY KEY (likeUserId, targetType, targetId),
    CONSTRAINT FK_LikeUser FOREIGN KEY (likeUserId) REFERENCES Users(userId)
);

CREATE TABLE attendees (
    entryId INT IDENTITY(1,1),
    attendeeId INT NOT NULL,
    activityId INT NOT NULL,
    isApproved BIT DEFAULT 0,
    isCompleted BIT DEFAULT 0,
    registeredAt DATETIME DEFAULT GETDATE(),
    PRIMARY KEY (entryId, activityId),
    CONSTRAINT FK_Attendee FOREIGN KEY (attendeeId) REFERENCES Users(userId),
    CONSTRAINT FK_Activity FOREIGN KEY (activityId) REFERENCES activities(activitiesId)
);
CREATE TABLE users_comments (
    userCommentId INT IDENTITY(1,1) PRIMARY KEY,
    commenterId INT NOT NULL,
    commentedId INT NOT NULL,
    activityRefId INT NOT NULL,
    score INT CHECK (score >= 1 AND score <= 5),
    content NVARCHAR(255),
    createdAt DATETIME DEFAULT GETDATE(),
    updatedAt DATETIME DEFAULT GETDATE(),
    UNIQUE (commenterId, commentedId, activityRefId),
    CONSTRAINT FK_Commenter_UsersComments FOREIGN KEY (commenterId) REFERENCES Users(userId),
    CONSTRAINT FK_Commented_UsersComments FOREIGN KEY (commentedId) REFERENCES Users(userId),
    CONSTRAINT FK_Activity_UsersComments FOREIGN KEY (activityRefId) REFERENCES activities(activitiesId)
);

CREATE TABLE users_ban (
    userId INT NOT NULL,
    banedUserId INT NOT NULL,
    PRIMARY KEY (userId , banedUserId),
    FOREIGN KEY (userId) REFERENCES users(userId),
    FOREIGN KEY (banedUserId) REFERENCES users(userId)
);
CREATE TABLE report (
    reportId int IDENTITY(1,1) PRIMARY KEY NOT NULL,
    reportUserId int NOT NULL,
    reportItem nvarchar(5) NOT NULL,
    reportItemId int NOT NULL,
    reportType nvarchar(50) NOT NULL,
    reportReason nvarchar(200) NOT NULL,
    reportStatus char DEFAULT 'P' NOT NULL,
    FOREIGN KEY (reportUserId) REFERENCES users(userId)
);
CREATE TABLE notification (
    notificationId int IDENTITY(1,1) NOT NULL PRIMARY KEY ,
    notificationUserId int NOT NULL,
    notificationTitle nvarchar(20) NOT NULL,
    notificationContent nvarchar(max) NOT NULL,
    notificationTime datetime NOT NULL DEFAULT GETDATE(),
    notificationRead char NOT NULL DEFAULT '0',
    isGlobal char NOT NULL DEFAULT '0',
    FOREIGN KEY (notificationUserId) REFERENCES users(userId)
);
CREATE TABLE social_follow (
   followerId INT NOT NULL,
   followeeId INT NOT NULL,
    PRIMARY KEY (followerId, followeeId),
    FOREIGN KEY (followerId) REFERENCES users_profile(userId),
    FOREIGN KEY (followeeId) REFERENCES users_profile(userId)
);
CREATE TABLE chatroom (
    chatroomId INT IDENTITY(1,1) PRIMARY KEY,
    chatType CHAR(1) NOT NULL CHECK (chatType IN ('G', 'P')),
    createDate date NOT NULL DEFAULT GETDATE()
);
CREATE TABLE chatroom_act (
   chatroomId INT NOT NULL,
   actId INT NOT NULL,
    PRIMARY KEY (chatroomId, actId),
    FOREIGN KEY (chatroomId) REFERENCES chatroom(chatroomId),
    FOREIGN KEY (actId) REFERENCES activities(activitiesId)
);
CREATE TABLE chatroom_details (
    chatroomId INT NOT NULL,
    userId INT NOT NULL,
    readQty BIGINT DEFAULT 0,
    joinDate date NOT NULL DEFAULT GETDATE(),
    PRIMARY KEY (chatroomId, userId),
    FOREIGN KEY (chatroomId) REFERENCES chatroom(chatroomId),
    FOREIGN KEY (userId) REFERENCES users_profile(userId)
);
CREATE TABLE chatroom_messages (
    messageId INT IDENTITY(1,1) PRIMARY KEY,
    chatroomId INT NOT NULL,
    senderId INT NOT NULL,
    content NVARCHAR(200) NOT NULL,
    timestamp DATETIME2 NOT NULL,
    FOREIGN KEY (chatroomId) REFERENCES chatroom(chatroomId),
    FOREIGN KEY (senderId) REFERENCES users_profile(userId)
);
CREATE TABLE matching (
    userId INT NOT NULL,
    userPreferId INT NOT NULL,
    likeOrNot BIT NOT NULL,
    matchedDate DATE NOT NULL,
    matchedSuccessfullyDate DATE,
    chatroomId INT,
    CONSTRAINT PK_matching PRIMARY KEY (userId, userPreferId),
    CONSTRAINT CK_likeOrNot CHECK (likeOrNot IN (0, 1)),
    CONSTRAINT FK_matching_userId FOREIGN KEY (userId) REFERENCES users_profile(userId),
    CONSTRAINT FK_matching_userPreferId FOREIGN KEY (userPreferId) REFERENCES users_profile(userId),
    CONSTRAINT FK_matching_chatroomId FOREIGN KEY (chatroomId) REFERENCES chatroom(chatroomId)
);
create table managers(
	managerId int primary key not null identity(1,1),
	email nvarchar(50) unique not null,
	password nvarchar(100) not null,
	name nvarchar(50) not null,
	gender char(1) not null,
	birth date not null,
	department nvarchar(50) not null,
	position nvarchar(50) not null,
	picture varchar(100) not null,
	deleteState bit default 0 not null
);
CREATE TABLE managers_manage (
    id INT IDENTITY(1,1) PRIMARY KEY,
    nonVipMatchingQty INT NOT NULL,
    vipMatchingQty INT NOT NULL
);
