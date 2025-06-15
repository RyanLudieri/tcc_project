package com.example.projeto_tcc.enums;

import com.example.projeto_tcc.entity.*;

public enum ProcessType {

    ACTIVITY {
        @Override
        public Activity createInstance() {
            return new Activity();
        }
    },
    TASK_DESCRIPTOR {
        @Override
        public Activity createInstance() {
            return new TaskDescriptor();
        }
    },
    MILESTONE {
        @Override
        public Activity createInstance() {
            return new Milestone();
        }
    },
    PHASE {
        @Override
        public Activity createInstance() {
            return new Phase();
        }
    },
    ITERATION {
        @Override
        public Activity createInstance() {
            return new Iteration();
        }
    },
    DELIVERY_PROCESS {
        @Override
        public Activity createInstance() {
            return new DeliveryProcess();
        }
    };

    public abstract Activity createInstance();
}
