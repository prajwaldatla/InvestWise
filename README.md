# InvestWise 📊

**Investment Portfolio Risk & Diversification Analyzer**

InvestWise is a Java-based desktop application that helps users manage their stock and mutual fund investments while understanding portfolio performance, diversification, and historical risk.

Built as an Object-Oriented Programming (OOPS) Lab project, it combines investment tracking with basic financial data analysis through a graphical user interface.

## ✨ Features

### 1. Portfolio Management
- Add, edit, and delete stock and mutual fund investments.
- View investment details in a consolidated portfolio.
- Search and filter recorded investments.
- Track purchase price, current price, quantity, and investment value.

### 2. Portfolio Performance
- Calculate initial investment and current portfolio value.
- Calculate profit or loss for individual investments.
- Calculate percentage returns.

### 3. Asset Allocation
- Visualize how investments are distributed across asset types.
- Understand the composition of the portfolio.

### 4. Mutual Fund Overlap Analysis
- Compare the underlying holdings of mutual funds.
- Identify common stocks across selected funds.
- Understand potential concentration in fund holdings.

### 5. Historical Risk Analysis
- Analyze historical stock price data.
- Calculate daily returns and statistical risk measures.
- Examine historical volatility.

### 6. Correlation Analysis
- Calculate Pearson correlation between investment return series.
- Understand how two investments have historically moved in relation to each other.

### 7. CSV Import
- Import investment records using CSV files.
- Preview records before importing.
- Validate data and identify duplicate or invalid entries.
- Store portfolio information locally.

## 🛠️ Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core programming language |
| Java Swing | Desktop graphical user interface |
| CSV | Local data storage and data import |
| Java Collections | Managing investment records |
| Object-Oriented Programming | Application architecture |

## 📁 Project Structure

```text
InvestWise/
│
├── src/
│   ├── Main.java
│   ├── Dashboard.java
│   ├── Investment.java
│   ├── Stock.java
│   ├── MutualFund.java
│   ├── Portfolio.java
│   ├── DataManager.java
│   ├── OverlapDetector.java
│   ├── RiskAnalyzer.java
│   ├── CorrelationCalculator.java
│   ├── ReturnCalculator.java
│   └── InvestWiseTest.java
│
├── data/
│   ├── sample_investments_dataset.md
│   ├── sample_historical_stock_prices_dataset.md
│   └── sample_mutual_fund_holdings_dataset.md
│
├── .gitignore
└── README.md
```

## 🚀 Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or later
- VS Code or another Java-compatible IDE
- macOS, Windows, or Linux

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/prajwaldatla/InvestWise.git
```

**2. Navigate into the project**

```bash
cd InvestWise
```

**3. Compile the Java source files**

```bash
mkdir -p out
javac -d out src/*.java
```

**4. Run the application**

```bash
java -cp out Main
```

The InvestWise desktop application should now launch.

## 🧠 OOP Concepts Demonstrated

The project applies several core Object-Oriented Programming concepts:

- **Abstraction:** The `Investment` abstract class defines the common structure and behaviour of investments.
- **Inheritance:** `Stock` and `MutualFund` extend the `Investment` class.
- **Polymorphism:** Different investment types can be handled through their common parent class.
- **Encapsulation:** Investment data and operations are organised within their respective classes.
- **Composition:** The portfolio manager works with collections of investment objects.
- **Exception Handling:** Used to handle invalid inputs and file-related errors.

## 📊 Sample Data

The project includes sample datasets for investments, historical stock prices, and mutual fund holdings.

These datasets are illustrative and are intended for demonstrating the application's functionality. They should not be interpreted as live market data.

## 🎯 Project Objective

The objective of InvestWise is to demonstrate how Java and Object-Oriented Programming can be applied to a practical financial data analysis problem.

The application focuses on helping users understand their existing investments through portfolio tracking, diversification analysis, and historical statistical measures.

**Disclaimer:** InvestWise is an educational project, not a financial advisory tool. Its calculations and sample data should not be used as the sole basis for investment decisions.

## 👨‍💻 Author

**Prajwal Datla**

GitHub: [@prajwaldatla](https://github.com/prajwaldatla)

---

*Built as an Object-Oriented Programming Lab project.*
