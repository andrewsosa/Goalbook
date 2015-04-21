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
    var list:String?
    var completed = false
    var dueDays:Int?
    var dueDate:NSDate?
    var dayInSeconds = 86500.0 // MAY BE A PROBLEM AREA LATER WHEN IMPLEMENTING TIME
    var discription:String?
    let uuid:String?
    
    var dueDateComponent = NSDateComponents()
    
    init(task: String){
        
        self.taskName = task
        
        var currentDate = NSDate()
        dueDate = currentDate
        
        var myCalendar:NSCalendar = NSCalendar(calendarIdentifier: NSGregorianCalendar)!
        var dueDateComponents = myCalendar.components(NSCalendarUnit.CalendarUnitDay, fromDate: dueDate!)
        
        var durationDateComponents = myCalendar.components(NSCalendarUnit.CalendarUnitDay, fromDate: currentDate, toDate: dueDate!, options: nil)
        
        dueDays = durationDateComponents.day
        println("test \(dueDays)")
        
        uuid = NSUUID().UUIDString
        
        
        
    }
    
    func dateToString() -> String {
    
        var myCalendar:NSCalendar = NSCalendar(calendarIdentifier: NSGregorianCalendar)!
        var dueDateComponentDay = myCalendar.components(NSCalendarUnit.CalendarUnitDay, fromDate: dueDate!)
        var dueDateComponentMonth = myCalendar.component(NSCalendarUnit.CalendarUnitMonth, fromDate: dueDate!)
        var dueDateComponentYear = myCalendar.component(NSCalendarUnit.CalendarUnitYear, fromDate: dueDate!)
        
        var dueDateDay = dueDateComponentDay.day
        var dueDateMonth = dueDateComponentMonth
        var dueDateYear = dueDateComponentYear
        
        var tempString = self.getMonth(dueDateMonth) + " \(dueDateDay), \(dueDateYear)"
        
        return tempString
    }
    
    func dateUp() {
    
        var tempDate = dueDate
        var newDate = tempDate?.dateByAddingTimeInterval(dayInSeconds)
        dueDate = newDate
        upDateDueDays()
    }
    
    func dateDown() {
    
        var tempDate = dueDate
        var newDate = tempDate?.dateByAddingTimeInterval(-dayInSeconds)
        dueDate = newDate
        upDateDueDays()
    
    }
    
    func upDateDueDays() {
        println(dueDays)

        var currentDate = NSDate()
        
        var myCalendar:NSCalendar = NSCalendar(calendarIdentifier: NSGregorianCalendar)!
        // var dueDateComponents = myCalendar.components(NSCalendarUnit.CalendarUnitDay, fromDate: dueDate!)
        
        var durationDateComponents = myCalendar.components(NSCalendarUnit.CalendarUnitDay, fromDate: currentDate, toDate: dueDate!, options: nil)
        
        dueDays = durationDateComponents.day
        println(dueDays)
        
    }
    
    func Complete() {
    
    self.completed = true
    println("completed ran")
    
    }
    
    func getComp() {
        println(self.completed)
    }
    
    func getDueDays() -> Int {
        
        return self.dueDays!
        
    }
    
    
    func getMonth(month: Int) -> String {
    
        switch month {
        
        case 1:
            return "Jan."
        case 2:
            return "Feb."
        case 3:
            return "Mar."
        case 4:
            return "Apr."
        case 5:
            return "May"
        case 6:
            return "June"
        case 7:
            return "July"
        case 8:
            return "Aug."
        case 9:
            return "Sep."
        case 10:
            return "Oct."
        case 11:
            return "Nov."
        case 12:
            return "Dec."
        
        default:
            return "Broken!"
            
        }
    
    }

}