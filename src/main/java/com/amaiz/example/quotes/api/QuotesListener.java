package com.amaiz.example.quotes.api;

import java.util.List;

public interface QuotesListener {

    // expected to be invoked in one thread
    void quotesReceived(List<Quote> quotes);
}
