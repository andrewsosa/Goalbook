//
//  ViewController.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/19/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit

var arrayOfTasks:[Task] = []

var passController:ViewController?
var delegate: CenterViewControllerDelegate?
var listPass: List!


class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate {
    
    @IBOutlet weak var topBar: UIView!
    
    @IBOutlet weak var entryBar: UIView!
    
    @IBOutlet weak var myTabelView: UITableView!
    
    @IBOutlet weak var textEntry: UITextField!
    
    @IBOutlet weak var listLabel: UILabel!
    
    var currentList: List!
    
    @IBAction func slidePress(sender: AnyObject) {
        
        println(delegate)
        delegate?.toggleLeftPanel?()
        
        
    }
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
        
        let app = UIApplication.sharedApplication()
        let height = app.statusBarFrame.size.height
        
        self.navigationController?.navigationBarHidden = true // TO HIDE NAVIGATION BAR
        
        passController = self
        
        arrayOfLists.append(item1)
        
        currentList = arrayOfLists[0]
        
        listLabel.text = currentList.listName
        
        self.prefersStatusBarHidden()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func AddListItem()
    {
    
        if textEntry.text != "" {
            
            var tempString:String = textEntry.text
            var newTask:Task = Task(task: tempString)
            currentList.addTask(tempString)
            arrayOfTasks.append(newTask)
            
            
            textEntry.text = ""
            myTabelView.reloadData()
        }
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        //textField.resignFirstResponder()
        AddListItem()
        return true
    }
    
    func tableView(tableView: UITableView, willSelectRowAtIndexPath indexPath: NSIndexPath) -> NSIndexPath? {
        let task = arrayOfTasks[indexPath.row]
        
        var detailedViewController: DetailViewController = self.storyboard?.instantiateViewControllerWithIdentifier("TaskPage") as DetailViewController
        
        detailedViewController.taskString = task.taskName
        
        self.presentViewController(detailedViewController, animated: true, completion: nil)
        
        return indexPath
    }
    
    func call() {
    
        listLabel.text = currentList.listName
        arrayOfTasks.removeAll(keepCapacity: false)
        
        for var i = 0; i < currentList.listSize; i++ {
            
            var task = currentList.getTask(i)
            arrayOfTasks.append(task)
            
        }
        myTabelView.reloadData()
    }
    
} // END OF VIEW CONTROLLER

