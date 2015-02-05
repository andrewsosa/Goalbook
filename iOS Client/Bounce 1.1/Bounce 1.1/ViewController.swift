//
//  ViewController.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/19/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit
var task1 = Task(task: "Walk the Dog")
var task2 = Task(task: "Buy groceries")
var arrayOfTasks:[Task] = []
var passTask: Task?

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var topBar: UIView!
    
    @IBOutlet weak var entryBar: UIView!
    
    @IBOutlet weak var myTabelView: UITableView!
    
    @IBOutlet weak var textEntry: UITextField!
// ------------------------------------------REQUIRED FUNCTIONS----------------------------------------------------
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    
        return arrayOfTasks.count
    
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
    
        let cell: CustomCell = tableView.dequeueReusableCellWithIdentifier("Cell") as CustomCell
        
        let task = arrayOfTasks[indexPath.row]
        
        cell.mainLabel.text = task.taskName
        
        return cell
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        entryBar.layer.shadowColor = UIColor.blackColor().CGColor
        entryBar.layer.shadowOffset = CGSizeMake(5, 5)
        entryBar.layer.shadowRadius = 5
        
        entryBar.layer.shadowOpacity = 0.2
        
        topBar.layer.shadowColor = UIColor.blackColor().CGColor
        topBar.layer.shadowOffset = CGSizeMake(5, 5)
        topBar.layer.shadowRadius = 5
        
        topBar.layer.shadowOpacity = 0.2

    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func AddListItem()
    {
    
        if textEntry.text != "" {
            
            var tempString:String = textEntry.text
            var newTask = Task(task: tempString)
            println(newTask.taskName)
            arrayOfTasks.append(newTask)
            
            textEntry.text = ""
            
            myTabelView.reloadData()
        }
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        AddListItem()
        return true
    }
    
    func tableView(tableView: UITableView, willSelectRowAtIndexPath indexPath: NSIndexPath) -> NSIndexPath? {
        let task = arrayOfTasks[indexPath.row]
        passTask = task
        
        var detailedViewController: DetailViewController = self.storyboard?.instantiateViewControllerWithIdentifier("TaskPage") as DetailViewController
        
        detailedViewController.taskString = task.taskName
        
        self.presentViewController(detailedViewController, animated: true, completion: nil)
        
        return indexPath
    }
    
    
    

    
    
    
    
} // END OF VIEW CONTROLLER

