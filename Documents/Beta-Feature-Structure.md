```
                                                                                    Main Branch: Login Screen
                                                                            +-------------------------------------+
                                                                            | Features:                           |
                                                                            |   - Enter Username                  |
                                                                            |   - Enter Password                  |
                                                                            |   - Login Button                    |
                                                                            |   - Base credentials (admin, admin) |
                                                                            +-------------------------------------+
                                                                                              |
                                                                                              V
                                                                                  Main Branch: Admin Console
                                                                            +-------------------------------------+
                                                                            | Features:                           |
                                                                            |   - Button: Manage Menu Items       |
                                                                            |   - Button: Manage Users            |
                                                                            |   - Button: Inventory Management    |
                                                                            |   - Button: Sales Tracking          |
                                                                            |   - Logout Button                   |
                                                                            +-------------------------------------+
                                                 /-----------------------------------------------------------------------------------------------------\
                                                |                           |                                     |                                     |
                                                V                           V                                     V                                     V
                     Sub-Branch: Manage Menu Items               Sub-Branch: Manage Users          Sub-Branch: Inventory Management         Sub-Branch: Sales Tracking
                    +--------------------------------+      +--------------------------------+    +--------------------------------+    +--------------------------------+
                    | Features:                      |      | Features:                      |    | Features:                      |    | Features:                      |
                    |   - Item Name Input            |      |   - Username Input             |    |   - Track Ingredients          |    |   - Detailed Reporting:        |
                    |   - Description Input          |      |   - Password Input             |    |   - Low Stock Alerts           |    |     Generate reports on        |
                    |   - Price Input                |      |   - Email Input                |    +--------------------------------+    |     daily, weekly, and         |
                    |   - Add Button                 |      |   - Role Selection Checkboxes  |                                          |     monthly sales.             |
                    |   - Remove Button              |      |     (Waiter, Cook, Admin)      |                                          +--------------------------------+
                    |   - Edit Button                |      |   - Add Button                 |
                    |   - Confirm Button             |      |   - Remove Button              |
                    |   - Go Back Button             |      |   - Edit Button                |
                    +--------------------------------+      |   - Confirm Button             |
                                                            |   - Go Back Button             |
                                                            +--------------------------------+
                                          
                                                                                                |
                                                                                                |
                                                                                                V
                                                                                    Redirections from Login:
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                                                  Redirect: Waiter Landing Page
                                                                              +-------------------------------------+
                                                                              | Features:                           |
                                                                              |   - Menu Button                     |
                                                                              |   - New Order Button                |
                                                                              |   - Orders Button                   |
                                                                              |   - Logout Button                   |
                                                                              |                                     |
                                                                              +-------------------------------------+
                                                                            /                  |                    \
                                                                          /                    |                      \
                                                                        /                      |                        \  
                                                                      /                        |                          \
                                                                    /                          |                            \
                                                                  /                            |                              \
                                                                /                              |                                \
                                                              /                                |                                  \
                                                            /                                  |                                    \ 
                                                          /                                    |                                      \  
                                                        /                                      |                                        \
                                                        |                                      |                                         |
                                                        |                                      |                                         |
                                                        V                                      V                                         V
                                          Sub-Branch: Menu                           Sub-Branch: New Order                      Sub-Branch: Orders
                                          +--------------------------------+   +---------------------------------+     +----------------------------------+
                                          | Features:                      |   | Features:                       |     | Features:                        |
                                          |   - Lists all menu items       |   |   - Lists all menu items        |     |   - Lists orders on the left     |
                                          |   - Go Back Button             |   |   - Price listed next to items  |     |     with label "Order #"         |
                                          +--------------------------------+   |   - Checkboxes next to items    |     |   - Order summary appears on     |
                                                                               |   - Order summary updates when  |     |     the right when clicked       |
                                                                               |     checkboxes are checked      |     |   - Total at bottom              |
                                                                               |   - Quantity control for each   |     |   - "Mark as Completed" button   |
                                                                               |     item in summary ("- [ ] +") |     |     to remove and complete order |
                                                                               |   - Total at bottom reflects    |     |   - Go Back Button               |
                                                                               |     sum of items in summary     |     +----------------------------------+
                                                                               |   - Button to add items to      |
                                                                               |     order list                  |
                                                                               |   - Clears order summary and    |
                                                                               |     checkboxes when Add Order   |
                                                                               |     button is pressed           |
                                                                               |   - Go Back Button              |
                                                                               +---------------------------------+
                                                                               
                                                                               
                                                                                    Redirect: Cook Landing Page
                                                                              +-------------------------------------+
                                                                              | Features:                           |
                                                                              |   - Refresh Orders Button           |
                                                                              |   - Orders Tabs providing order     |
                                                                              |     summaries                       |
                                                                              |   - Order Queue: Display incoming   |
                                                                              |     orders in real-time with item   |
                                                                              |     details and special instructions|
                                                                              |   - Order Status Updates: Mark      |
                                                                              |     orders as "In Progress" or      |
                                                                              |     "Completed."                    |
                                                                              |   - Logout Button                   |
                                                                              +-------------------------------------+
                                                                              
                                  
                                                                                   Redirect: Admin Landing Page
                                                                              +-------------------------------------+
                                                                              | Features:                           |
                                                                              |   - Redirects to Admin Console      |
                                                                              |   - Logout Button                   |
                                                                              +-------------------------------------+
              
```
