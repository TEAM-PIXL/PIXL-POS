# PIXL POS

**TEAM**: PIXL POS <br>
**PROJECT**: Restaurant Management Application <br>
**DATE**: 2024-07-23 <br>
**VERSION**: <span style="color: #008585;">1.0</span> <br>
**AUTHORS**: Zachariah Craw (Project Manager & Backend Developer), Emily Lee (Lead Frontend Developer & UI/UX Design), Parker Rennie (Lead Backend Developer & Data Engineer), Jason Gunnawan (Backend Developer), Joshua Hecke (Frontend Developer)

# Table of Contents
1. [Project Brief: Restaurant Management Application](#1-project-brief-restaurant-management-application)
    - [Project Relevance](#project-relevance)
        - [Technological Advancements](#technological-advancements)
        - [Cost Efficiency](#cost-efficiency)
        - [Tourism Growth](#tourism-growth)
        - [Small Business Support](#small-business-support)
    - [Stakeholders](#stakeholders)
    - [Core Features (Must-Haves)](#core-features-must-haves)
    - [Extended Features (Could-Haves)](#extended-features-could-haves)
    - [Project Outline](#project-outline)
2. [Project Management](#2-project-management)
    - [Project Timeline](#project-timeline)
    - [Team Roles](#team-roles)
    - [Communication Plan](#communication-plan)
    - [Risk Management](#risk-management)
3. [Project Documentation](#3-project-documentation)
    - [JIRA Board](#jira-board-jira-board)
    - [Confluence](#confluence-confluence)
    - [GitHub Repository](#github-repository-github-repository)
    - [JavaDocs](#javadocs-javadocs)
4. [Project Demo](#4-project-demo)
    - [Demo Video](#demo-video)
    - [Demo Instructions](#demo-instructions)

## 1. Project Brief: Restaurant Management Application

This project aims to develop a Restaurant Management Application designed to enhance the operations
of small to medium-sized restaurants in Brisbane. The goal is to provide an affordable alternative
to expensive proprietary POS (Point of Sale) systems by covering essential aspects of restaurant
management. The application will offer essential features to meet the needs of restaurant staff,
including waiters, cooks, and administrators, such as order taking, kitchen workflow management, and
administrative control for business owners.

### Project Relevance

#### Technological Advancements
The restaurant industry is increasingly adopting digital solutions like self-service kiosks, mobile
ordering and contactless payments. Implementing an ordering system for waiters and cooks will allow
restaurants to keep up with these advancements, streamline operations, reduce wait times, and enhance
the overall dining experience.

#### Cost Efficiency
Many small Brisbane restaurants face financial pressure due to rising operational costs. Traditional
POS systems are often expensive, making it hard for these businesses to adopt advanced solutions.
Offering a more affordable and scalable alternative helps small restaurants lower both initial and
ongoing costs, making it easier to access technology that improves operations, order accuracy, and
overall efficiency.

#### Tourism Growth
With the Brisbane Olympics in 2032, the influx of tourists will increase demand for efficient
restaurant management systems. An advanced POS system will help local restaurants manage higher
customer volumes and ensure a smooth dining experience.

#### Small Business Support
Small independent restaurants often struggle against larger chains that have more resources. By
providing an affordable and accessible management solution, this project supports local restaurants,
helping them thrive in a competitive market.


### Stakeholders

- **Restaurant Owners/Administrators**: Need a reliable system to manage menu items, user roles, and
  monitor overall restaurant performance.
- **Waiters**: Require a user-friendly interface to take orders efficiently, manage tables, and
  communicate seamlessly with the kitchen.
- **Cooks**: Need a clear, organised view of incoming orders to ensure timely and accurate
  preparation of meals.
- **Customers**: Indirectly benefit from faster service, accurate orders, and real-time information
  on menu items and wait times.

### Core Features (Must-Haves)

1. **Login System**
    - User authentication with roles: Admin, Waiter, Cook.
    - Basic credentials

2. **Admin Console**
    - Manage Menu Items: Add, edit, remove menu items with name, description, and price.
    - Manage Users: Add, edit, remove users with role assignments (Waiter, Cook, Admin).
    - View Sales Reports: Track sales by item, category, and time period.

3. **Waiter Interface**
    - New Order Creation: Select items from the menu, adjust quantities, and submit orders to the
      kitchen.
    - Order Management: View current orders, mark orders as completed, and manage table assignments.
    - Table Management: Assign orders to specific tables, manage table availability.

4. **Cook Interface**
    - Order Queue: Display incoming orders in real-time with item details and special instructions.
    - Order Status Updates: Mark orders as "In Progress" or "Completed."

5. **Menu Management**
    - Dynamic Menu Updates: Allow real-time updates to menu items by the admin.
    - Special Items: Feature daily or seasonal specials that are prominently displayed in the menu.

6. **Inventory Management**
    - Track Ingredients:2024-07-23 Monitor stock levels of ingredients and automatically update menu
      availability.
    - Low Stock Alerts: Notify admin when ingredient levels are low.

7. **Sales Tracking**
    - Detailed Reporting: Generate reports on daily, weekly, and monthly sales.

8. **User Profiles**
    - Profile Management: Allow users to update their details and preferences.
    - Role-Specific Access: Ensure users can only access features relevant to their role.

### Extended Features (Could-Haves)

1. **Live Wait Times**
    - Display real-time wait times for tables based on current restaurant occupancy and queue lengths.

2. **Reservation System**
    - Allow customers to book tables in advance through the app, with automatic confirmation and
      reminders.

3. **Customer Feedback**
    - Integrated feedback system where customers can rate their experience and leave comments.

4. **Promotions & Discounts**
    - Manage promotional offers and discounts, which can be applied automatically at checkout.

5. **Analytics Dashboard**
    - Advanced analytics for administrators, including customer demographics, peak hours, and popular
      menu items.

**Draft User Stories**: [User Stories](./Documents/Draft-User-Stories.md) <br>
**Revised User Stories**: [User Stories](./Documents/User+Stories.pdf) <br>
**Beta-Feature-Structure**: [Beta-Feature-Structure](./Documents/Beta-Feature-Structure.md)

### Project Outline

This project will be developed in stages, starting with the core features necessary for a functional
POS system. The initial focus will be on creating a stable, user-friendly platform for restaurant
staff to manage daily operations efficiently. Once the core features are implemented and tested, the
project will gradually expand to include the extended features, enhancing the application's overall
value.

## 2. Project Management

### Project Timeline

- **Phase 1: Planning & Design (2 weeks)**
  - Define project scope, requirements, and milestones.
  - Create wireframes and mockups for UI/UX design.
  - Develop database schema and system architecture.
  - Assign tasks and set up version control.
  - Finalize project plan and timeline.
  - Setup CI/CD pipeline for automated testing.
  - Setup JIRA and Confluence for project management.
  - Set initial JIRA issues, epics, and sprint planning.
  - **Start Date**: 2024-08-06
<p>

- **Phase 2: Core Backend Functionality (1 week)**
  - Implement user authentication and role-based access control.
  - Develop backend services for menu management, order processing, and user management.
  - Integrate database functionality for inventory management.
  - Implement basic reporting features.
  - **Start Date**: 2024-08-20
<p>

- **Phase 3: Core Feature Development (3 weeks)**
    - Develop frontend interfaces for admin console, waiter interface, and cook interface.
    - Implement menu management, order processing, and user management features.
    - Integrate frontend and backend components for seamless operation.
    - Test core features for functionality and usability.
    - **Start Date**: 2024-09-03
<p>

- **Phase 4: Extended Feature Development (3 weeks)**
    - Implement extended features such as automatic state tracking, advanced order systems, dashboard widgets, stock management, and analytics dashboards.
    - Enhance user profiles and role-specific access.
    - Conduct user acceptance testing and gather feedback for improvements.
    - **Start Date**: 2024-09-24
<p>

- **Phase 5: Maintenance, Documentation & Support (Ongoing)**
    - Address bug fixes and performance issues.
    - Provide ongoing support and maintenance for the application.
    - Monitor system performance and user feedback for continuous improvement.
    - Finalize documentation and prepare strategic recommendations for future enhancements.
    - Present final project report and deliverables.
    - **Start Date**: 2024-10-15
<p>

- **End Date**: 2024-10-22
- **Project Duration**: 3 Months

### Team Roles

- **Zachariah Craw (Project Manager & Backend Developer)**
  - Responsible for overall project management, task allocation, and timeline adherence.
  - Backend development tasks, including but not limited to MVC design, API development, system architecture, systems integration, and testing.
  - Project lead on JIRA and Confluence, ensuring documentation and task tracking are up-to-date.
  - Meeting facilitation and lead communication with stakeholders including but not limited to scheduling, progress updates, and issue resolution / allocation.
  - **Start Date**: 2024-07-23
<p>

- **Emily Lee (Lead Frontend Developer & UI/UX Design)**
    - Responsible for frontend development tasks, including but not limited to UI/UX design, wire-framing, mockups, frontend development, and testing.
    - Lead on design tools such as Figma, SceneBuilder, and more.
    - Collaborate with backend developers to ensure seamless integration of frontend and backend components.
    - Convert design concepts into functional user interfaces with a focus on usability and accessibility.
    - CSS design and implementation for responsive GUI applications.
    - Lead reviewer for frontend code quality and adherence to design specifications.
    - **Start Date**: 2024-07-30
<p>

- **Parker Rennie (Lead Backend Developer & Data Engineer)**
    - Responsible for backend development tasks, including but not limited to database design, API development, system architecture, systems integration, and testing.
    - Lead on backend technologies such as Java, SQL, Docker, GitHub Actions, and more.
    - Collaborate with frontend developers to ensure seamless integration of frontend and backend components.
    - Develop and maintain database schema, ensuring data integrity and performance.
    - Lead reviewer for backend code quality and adherence to architectural standards.
    - Lead Java developer for backend services and API development.
    - **Start Date**: 2024-07-23
<p>

- **Jason Gunnawan (Backend Developer)**
    - Backend development tasks, including but not limited to controller design, authentication development, system architecture, systems integration, and testing.
    - Collaborate with frontend developers to ensure seamless integration of frontend and backend components. Assisting in any backend development tasks as required.
    - Develop and maintain database functions, error handling, and performance tuning.
    - Java developer for backend services and authentication development.
    - **Start Date**: 2024-07-23
<p>

- **Joshua Hecke (Frontend Developer)**
    - Frontend development tasks, including but not limited to UI/UX design, wire-framing, mockups, frontend development, and testing.
    - Collaborate with backend developers to ensure seamless integration of frontend and backend components. Assisting in any frontend development tasks as required.
    - Design choices and visual elements for the user interface, ensuring a consistent and intuitive user experience.
    - FXML developer for frontend services and UI development.
    - Develop and maintain frontend components, ensuring cross-browser compatibility with numerous operating systems.
    - GUI design and implementation for responsive applications.
    - **Start Date**: 2024-07-23
<p>

### Communication Plan

- **Bi-Weekly Standup Meetings**: Every Tuesday and Sunday at 12:30pm and 5:30pm respectively.
  - Approximately 30 minutes - 1 hour per meeting.
  - Discuss progress updates, blockers, and task allocation.
  - Review completed tasks and plan for upcoming work.
  - Address any issues or concerns raised by team members.
  - Review stakeholder feedback and adjust project plan as needed.
  - **Start Date**: 2024-07-29
<p>

- **Weekly Progress Reports**: Every Tuesday at 3:00pm.
  - Approximately 1.5 - 2 hours per meeting.
  - Compile individual progress reports from team members.
  - Summarize completed tasks, ongoing work, and upcoming milestones.
  - Highlight any challenges faced and proposed solutions.
  - Share reports with the team and stakeholders for transparency.
  - Evaluate sprint progress and adjust timelines if necessary.
  - Align team goals and priorities for the upcoming week.
  - **Start Date**: 2024-08-06
<p>

- **Google Chat**: Ongoing communication for quick updates, questions, and informal discussions.
  - Use Google Chat for real-time messaging and quick responses.
  - Create dedicated channels for specific topics or teams (General, Backend, Frontend UI/UX).
  - Share relevant links, documents, and updates.
  - Encourage open communication and collaboration.
  - Online meetings can be scheduled as needed for in-depth discussions.
  - Conflicts or issues can be escalated to the project manager for resolution.
  - **Start Date**: 2024-08-06
<p>

- **JIRA & Confluence**: Centralized project management tools for task tracking, documentation, and collaboration.
  - Use JIRA for issue tracking, sprint planning, and task assignment.
  - Create epics, user stories, and tasks to manage project progress.
  - Update task status, assignees, and priorities for transparency.
  - Use Confluence for documentation, meeting notes, and project resources.
  - Share project updates, reports, and documentation with the team.
  - Collaborate on design documents, system architecture, and user stories.
  - **Start Date**: 2024-08-06
<p>

- **GitHub Repository**: Version control and code collaboration for development tasks.
  - Use GitHub for code repository, version control, and code review.
  - Create branches for feature development, bug fixes, and enhancements.
  - Review code changes, provide feedback, and merge pull requests.
  - Ensure code quality, consistency, and adherence to coding standards.
  - Automate testing and deployment processes using GitHub Actions.
  - **Start Date**: 2024-07-23

### Risk Management

- **Technical Risks**
  - **Database Failure**: N/A Due to the nature of SQLite being a file-based database, the risk of database failure is minimal.
  - **Security Vulnerabilities**: Conduct regular security audits and implement best practices to protect user data, i.e. encryption, secure authentication, and input validation. Additionally, store API keys and sensitive information securely.
  - **Integration Issues**: Test system components thoroughly to ensure seamless integration and functionality. CI/CD pipeline will be used to automate testing and deployment processes.
  - **Performance Bottlenecks**: Monitor system performance and optimize code for efficiency.
  - **Third-Party Dependencies**: Have contingency plans in place for third-party service outages or changes. i.e. use of local storage for critical data.
<p>

- **Operational Risks**
  - **Resource Constraints**: Allocate tasks effectively, manage workload, and adjust timelines as needed. There is a limit on server resources, specifically CI/CD pipeline resources. And documentation storage resources.
  - **Scope Creep**: Regular meetings and progress reports to ensure project stays on track. Review project scope and requirements to prevent unnecessary feature additions. Keep documentation up-to-date.
  - **Communication Breakdown**: Maintain open communication channels, conduct regular meetings, and address conflicts promptly. Apply P.I.N (Position, Interests, Needs) model for effective communication.
  - **Team Member Availability**: Plan for contingencies in case of team member unavailability. Cross-train team members and document key processes. Use pair programming and code reviews to share knowledge. Assign tasks based on team members' strengths and availability.
  - **Stakeholder Expectations**: Manage stakeholder expectations through regular updates, feedback sessions, and transparent communication. Take on board feedback and adjust project plan as needed. Clearly work to CRA (Clear, Realistic, Achievable) goals.
<p>

- **External Risks**
  - **Economic Factors**: Monitor cost implications and adjust project budget as needed. Use cost-effective solutions and open-source tools where possible. Regularly review project expenses and adjust resource allocation. Resources such as JIRA, Confluence, GitHub Teams and Azure are mostly free but can accrue cost.
  - **Technological Changes**: Stay updated on technological advancements and industry standards. Make sure dependencies are secure and up-to-date to mitigate risks. Regularly review and update software libraries and dependencies. Use LTS (Long Term Support) versions where possible.
  - **Task Changes**: Be prepared for changes in project requirements or priorities. Use agile methodologies to adapt to changing circumstances. Prioritize tasks based on business value and impact. Conduct regular retrospectives to review and adjust project plan.

## 3. Project Documentation

### JIRA Board: [JIRA Board](https://teampixl.atlassian.net/jira/software/projects/RDM/boards/6)
### Confluence: [Confluence](https://teampixl.atlassian.net/wiki/spaces/SD/overview)
### GitHub Repository: [GitHub Repository](https://github.com/TEAM-PIXL/PIXL-POS)
### JavaDocs: [JavaDocs](https://team-pixl.github.io/PIXL-POS/PIXL.POS/module-summary.html)

## 4. Project Demo

### Demo Video

[![Demo Video](https://youtube.com.au)]

### Demo Instructions

