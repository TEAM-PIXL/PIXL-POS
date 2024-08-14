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
                                                        |   - Logout Button                   |
                                                        +-------------------------------------+
                                                        |                                     |
                                                        |                                     |
                                                        V                                     V
                                              Sub-Branch: Manage Menu Items               Sub-Branch: Manage Users
                                          +--------------------------------+         +--------------------------------+
                                          | Features:                      |         | Features:                      |
                                          |   - Item Name Input            |         |   - Username Input             |
                                          |   - Description Input          |         |   - Password Input             |
                                          |   - Price Input                |         |   - Email Input                |
                                          |   - Add Button                 |         |   - Role Selection Checkboxes  |
                                          |   - Remove Button              |         |     (Waiter, Cook, Admin)      |
                                          |   - Edit Button                |         |   - Add Button                 |
                                          |   - Confirm Button             |         |   - Remove Button              |
                                          |   - Go Back Button             |         |   - Edit Button                |
                                          +--------------------------------+         |   - Confirm Button             |
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
                                    |                                       |                                         |
                                    |                                       |                                         |
                                    V                                       V                                         V
                      Sub-Branch: Menu                           Sub-Branch: New Order                        Sub-Branch: Orders
                      +--------------------------------+   +---------------------------------+        +----------------------------------+
                      | Features:                      |   | Features:                       |        | Features:                        |
                      |   - Lists all menu items       |   |   - Lists all menu items        |        |   - Lists orders on the left     |
                      |   - Go Back Button             |   |   - Price listed next to items  |        |     with label "Order #"         |
                      +--------------------------------+   |   - Checkboxes next to items    |        |   - Order summary appears on     |
                                                           |   - Order summary updates when  |        |     the right when clicked       |
                                                           |     checkboxes are checked      |        |   - Total at bottom              |
                                                           |   - Quantity control for each   |        |   - "Mark as Completed" button   |
                                                           |     item in summary ("- [ ] +") |        |     to remove and complete order |
                                                           |   - Total at bottom reflects    |        |   - Go Back Button               |
                                                           |     sum of items in summary     |        +----------------------------------+
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
                            |   - Coming Soon                     |
                            |   - Logout Button                   |
                            +-------------------------------------+
              
                            Redirect: Admin Landing Page
                            +-------------------------------------+
                            | Features:                           |
                            |   - Redirects to Admin Console      |
                            |   - Logout Button                   |
                            +-------------------------------------+
```
