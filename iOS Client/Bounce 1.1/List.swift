//
//  List.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/30/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import Foundation

class List{

    var listName:String?
    var arrayOfTasks:[Task] = []
    var listSize:Int?
    
    init(name: String){
    
        self.listName = name
        arrayOfTasks = []
        listSize = 0
    }
    
    func addTask(task: String){
    
        var newTask = Task(task: task)
        self.arrayOfTasks.append(newTask)
        listSize = listSize! + 1
        
    }
    
    func getTask(index: Int) -> Task {
    
        var tempTask:Task = arrayOfTasks[index]
    
        return tempTask
    }
}// END OF CLASS