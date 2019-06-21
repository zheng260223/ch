package com.workprocess.services.impl;

import com.workprocess.dao.EmergencytaskitemDAO;
import com.workprocess.services.EmergencytaskitemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmergencytaskitemServiceImpl implements EmergencytaskitemService {
    @Autowired
    EmergencytaskitemDAO emergencytaskitemDAO;
}
