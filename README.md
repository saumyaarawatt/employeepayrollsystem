# Employee Payroll System 

A simple Java-based console application for managing employee payrolls: tracking employees, processing salaries, keeping transaction logs, and generating reports.

## ✨ Features

- Add, update and remove employees  
- Calculate pay (basic salary, allowances, deductions)  
- Maintain a transactions log (`transactions.log`) for audit & tracking  
- Generate summary reports (e.g., total payroll cost, employee breakdowns)  
- Persist data in a simple file / Java object structure (or extendable to DB)  
- Modular code architecture (src folder contains main logic)  

## 🧩 Technologies & Requirements

- Java (version 8 or above)  
- No external database required (file-based storage)  
- Optional: IntelliJ IDEA or Eclipse for development  
- Git for version control

## 📦 How to Run

1. Clone the repository:
   ```bash
   git clone https://github.com/okayyyabhishek/employeepayrollsystemm.git
2. Open the project in any Java IDE (IntelliJ recommended).
3. Ensure the src/ folder is marked as Source Root.
4. Run: Main.java
5. Use the menu/interface to:
      - Add employees
      - Enter work hours
      - Generate salary
      - View salary logs
   
## 🗣️ Usage

- On launch, you’ll be presented with a menu (Add Employee / Remove Employee / Process Payroll / View Reports)
- Follow prompts to add employee details (name, ID, salary, allowances, deductions)
- Choose “Process Payroll” to compute net salary and log the transaction
- Choose “View Reports” to display aggregate payroll summaries
- The file transactions.log will record each payroll transaction with timestamp and details

## 🛠️ Configuration & Logging

- All transactions are appended to transactions.log.
- If you wish to change the logging behavior (e.g., format, file location), update the logging logic in the source file where transactions are written.
- To extend storage to a database (MySQL, SQLite), you can refactor the persistence layer in src/dao (if exists) or create one.

## 📝 License & Acknowledgements

- This repository is released under the MIT License. See LICENSE file for details.
- Special thanks to all open-source Java utilities and logging frameworks which inspired this project.
