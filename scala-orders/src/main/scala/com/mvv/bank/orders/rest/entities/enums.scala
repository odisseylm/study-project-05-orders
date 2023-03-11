package com.mvv.bank.orders.rest.entities

enum OrderType derives CanEqual :
  case MARKET_ORDER
     , LIMIT_ORDER
     , STOP_ORDER


enum Side derives CanEqual :
  case CLIENT, BANK_MARKET


enum BuySellType derives CanEqual :
  case BUY, SELL


enum DailyExecutionType derives CanEqual :
  case DAY_ONLY
     , GTC


enum OrderState derives CanEqual :
  case UNKNOWN
     , TO_BE_PLACED
     , PLACED
     , EXECUTED
     , EXPIRED
     , CANCELED
