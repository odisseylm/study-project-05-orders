package com.mvv.bank.orders.rest.entities

enum OrderType :
  case MARKET_ORDER
     , LIMIT_ORDER
     , STOP_ORDER


enum Side :
  case CLIENT, BANK_MARKET


enum BuySellType :
  case BUY, SELL


enum DailyExecutionType :
  case DAY_ONLY
     , GTC


enum OrderState :
  case UNKNOWN
     , TO_BE_PLACED
     , PLACED
     , EXECUTED
     , EXPIRED
     , CANCELED
