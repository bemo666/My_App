package com.ikea.myapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlanHeader implements Serializable {

    private PlanType type;
    private List<Plan> plans;


    public PlanHeader(PlanType type, List<Plan> plans) {
        this.type = type;
        this.plans = plans;
    }

    public PlanType getType() {
        return type;
    }

    public void setType(PlanType type) {
        this.type = type;
    }

    public List<Plan> getPlans() {
        return plans;
    }

    public void setPlans(List<Plan> plans) {
        this.plans = plans;
    }

    public void addPlan(Plan plan){
        if(plans == null)
            plans = new ArrayList<>();
        plans.add(plan);
    }
}
