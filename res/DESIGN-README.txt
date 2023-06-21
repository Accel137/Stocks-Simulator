The program is designed with model MVC
1. Models
entities in package model, like following
Interfaces: FileType, Persist, Portfolio
Abstract Classes: AbstractPortfolio, AbstractPersist
Class: CSV, FlexPortfolio, InflexPortfolio, PersistFlexPortfolio, PersistInflexPortfolio, PersistStock, PersistUsers, Stock, StockList, User

they are responsible for representing an entity.

2. Controllers
Controller in package controller is used to process different actions from user, like following
    user login verification
    portfolio create
    and so on.
it is responsible for logic process with different models.

3. GUI View
entities in package view.gui, like following
DollarCost, Handler, LoginView, MainView, MyStock, StockTableModel, Trade

GUI is used as view in this program.
Handler is responsible for provide common resourses.

4. New Stock Data Api Alpha Vantage Integrate
We add a package api. We create a new interface API and an abstract class
AbstractAPI to represent generic API source.
We use Alpha Vantage as a source to:
1. Provide stocks information for users.
2. Compute newest value of portfolios.
3. Buy and sell stock based on the infomation from the API.
We can use this new data source by given required params.

5. Mutable Portfolio
I add some limitations in previous version to make portfolio immutable, and I remove them for new features like buy and sell and add new features in menu.

6. Dollar-Cost Averaging Investment
User can now use dollar-cost averaging to invest stocks.