package com.nazir.shopping.Model;

import java.util.List;

public class MyResponse {

    private long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
