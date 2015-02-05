//
//  Task.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/19/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import Foundation

class Task {

    var taskName: String = "blank"
    var date:String?
    var list:String?
    var completed = false
    
    init(task: String){
        self.taskName = task
    }

}