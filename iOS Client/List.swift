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
    var listImage:String?
    var arrayOfTasks:[Task] = []
    var listSize:Int?
    var listColor:String?
    
    init(name: String, image: String, color: String){
    
        self.listName = name
        self.listImage = image
        self.listColor = color
        arrayOfTasks = []
        listSize = 0
    }
    
    func addTask(task: String){
    
        var newTask = Task(task: task)
        self.arrayOfTasks.append(newTask)
        listSize = listSize! + 1
        
    }
    
    func addTask(task: Task){
        var newTask = task
        self.arrayOfTasks.append(newTask)
        listSize = listSize! + 1
    }
    
    func getTask(index: Int) -> Task {
    
        var tempTask:Task = arrayOfTasks[index]
    
        return tempTask
    }
    
    func removeTask(task: Task){
        
        var count:Int = 0
        
        for item in arrayOfTasks {
            
            if (task.uuid == item.uuid){
                
                arrayOfTasks.removeAtIndex(count)
                
            }
            count++
        }
    
    }
}   // END OF CLASS