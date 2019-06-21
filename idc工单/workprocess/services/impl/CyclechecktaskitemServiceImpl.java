package com.workprocess.services.impl;

import com.workprocess.dao.CyclechecktaskitemDAO;
import com.workprocess.services.CyclechecktaskitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CyclechecktaskitemServiceImpl implements CyclechecktaskitemService {
    @Autowired
    CyclechecktaskitemDAO cyclechecktaskitemDAO;
}
