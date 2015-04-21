//
//  ViewController.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/19/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit
import Parse

var arrayOfTasks:[Task] = []

var passController:ViewController?
var delegate: CenterViewControllerDelegate?
var listPass: List!
var passTask:Task?
var firstTime = true
var passCheck:Int!
var PCTask:Task!
var detailPassNum:Int?

class ViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, UITextFieldDelegate,CenterViewControllerDelegate  {
    
    @IBOutlet weak var listLabel: UILabel!
    
    @IBOutlet weak var topBar: UIView!
    
    @IBOutlet weak var entryBar: UIView!
    
    @IBOutlet weak var myTabelView: UITableView!
    
    @IBOutlet weak var textEntry: UITextField!
    
    var currentList: List!
    
    
    
    @IBAction func inboxTouched(sender: AnyObject) {
    
        delegate?.toggleLeftPanel?()
    
    }
    
    @IBAction func slidePress(sender: AnyObject) {
        
        delegate?.toggleLeftPanel?()
        
    }
// ------------------------------------------REQUIRED FUNCTIONS----------------------------------------------------
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    
        return arrayOfTasks.count
    
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
    
        let cell: CustomCell = tableView.dequeueReusableCellWithIdentifier("Cell") as! CustomCell
        
        let task = arrayOfTasks[indexPath.row]
        
        cell.mainLabel.text = task.taskName
        
        cell.cellNumber = indexPath.row
        
        if task.dueDays == 0 {
            cell.taskDate.text = "Due: Today"
        } else if task.dueDays == 1 {
            cell.taskDate.text = "Due: 1 day"
        } else if task.dueDays != nil{
            cell.taskDate.text = "Due: \(task.dueDays!) days"
        } else {
            cell.taskDate.text = "Due: No Deadline"
        }
        
        println("***********************")
        println(task.taskName)
        
        return cell
    }


    
     override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        passController = self
        println("999999999999")
        entryBar.layer.shadowColor = UIColor.blackColor().CGColor
        entryBar.layer.shadowOffset = CGSizeMake(5, 5)
        entryBar.layer.shadowRadius = 5
        entryBar.layer.shadowOpacity = 0.2
        
        let app = UIApplication.sharedApplication()
        let height = app.statusBarFrame.size.height
        
        self.navigationController?.navigationBarHidden = true // TO HIDE NAVIGATION BAR
        
        
        if firstTime == true {
        
        self.runFirstTime()
        firstTime = false
        loadData()
        
        }
        
        self.prefersStatusBarHidden()
        
        currentList = listPass
        
        listLabel.text = currentList.listName
        
        println(arrayOfTasks)
        
        topBarColor()
    }
    
    func runFirstTime() {
        
        arrayOfLists.append(item1)
        arrayOfLists.append(item2)
        arrayOfLists.append(item3)
        arrayOfLists.append(item4)
        arrayOfLists.append(item5)
        currentList = arrayOfLists[0]
        listPass = currentList

    }
    
    func topBarColor(){
    
        if currentList.listName == arrayOfLists[0].listName {
            topBar.backgroundColor = UIColor(0x03A9F4)
        } else if currentList.listName == arrayOfLists[1].listName {
            topBar.backgroundColor = UIColor(0xFFC107)
        } else if currentList.listName == arrayOfLists[2].listName {
            topBar.backgroundColor = UIColor(0x4CAF50)
        } else if currentList.listName == arrayOfLists[3].listName {
            topBar.backgroundColor = UIColor(0x9C27B0)
        } else if currentList.listName == arrayOfLists[4].listName {
            topBar.backgroundColor = UIColor(0x607D8B)
        }else {
            topBar.backgroundColor = UIColor(0x03A9F4)
        }
    
        println("FHRITP")
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
            
            
            
            if (currentList.listName == arrayOfLists[0].listName){
            
                newTask.list = "Unassigned"
                arrayOfLists[4].addTask(newTask)
                arrayOfTasks.append(newTask)
                arrayOfLists[0].addTask(newTask)
                arrayOfLists[3].addTask(newTask)
                
                
            }else if (currentList.listName == arrayOfLists[1].listName || currentList.listName == arrayOfLists[2].listName || currentList.listName == arrayOfLists[3].listName || currentList.listName == arrayOfLists[4].listName) {
                newTask.list = "Unassigned"
                arrayOfLists[4].addTask(newTask)
                arrayOfLists[0].addTask(newTask)
                arrayOfLists[3].addTask(newTask)
                
                
            } else {
            newTask.list = currentList.listName
            currentList.addTask(newTask)
            arrayOfTasks.append(newTask)
            arrayOfLists[0].addTask(newTask)
            arrayOfLists[3].addTask(newTask)
            
            }
            
            
            
            
            
            
            
            
            textEntry.text = ""
            myTabelView.reloadData()
        }
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool // called when 'return' key pressed. return NO to ignore.
    {
        //textField.resignFirstResponder()
        AddListItem()
        return true
    }
    
//    func tableView(tableView: UITableView, willSelectRowAtIndexPath indexPath: NSIndexPath) -> NSIndexPath? {
//        
////        let task = arrayOfTasks[indexPath.row]
////        detailPassNum = indexPath.row
////        println(detailPassNum)
////        //currentList.arrayOfTasks[indexPath.row].getComp()
////        passTask = currentList.arrayOfTasks[indexPath.row]
////        passTask!.getComp()
////        var detailedViewController: DetailViewController = self.storyboard?.instantiateViewControllerWithIdentifier("TaskPage") as! DetailViewController
////        
////        self.presentViewController(detailedViewController, animated: true, completion: nil)
////        
////        
////        return indexPath
//    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath)
    {
        
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        
        if segue.identifier == "something" {
            
            let index = myTabelView.indexPathForSelectedRow()!
            detailPassNum = index.row
            let task = arrayOfTasks[index.row]
            passTask = task
            let vc:DetailViewController = segue.destinationViewController as! DetailViewController
            vc.currentTask = task
            listPass = currentList
            
            self.myTabelView.deselectRowAtIndexPath(index, animated: true)
        }
        
        
    }
    
    
    
    func call() {
    
        
        println(currentList)
        listLabel.text = currentList.listName
        arrayOfTasks.removeAll(keepCapacity: false)
        
        for var i = 0; i < currentList.listSize; i++ {
            
            var task = currentList.getTask(i)
            arrayOfTasks.append(task)
            
        }
        myTabelView.reloadData()
        
        topBarColor()
    }
    
    func loadData() {
//        
//        var query = PFQuery(className: "Task")
//        query.findObjectsInBackgroundWithBlock { (array:[AnyObject]!, error:NSError!) -> Void in
//            
//            
//            if (error == nil) {
//                
//                let objects = array as [PFObject]
//                                
//                for var i = 0; i < objects.count; i++ {
//                    
//                    var temp1 = objects[i].objectForKey("name") as String
//                    
//                   self.currentList.addTask(temp1)
//                    
//                    var newTask = Task(task: temp1)
//                    
//                    arrayOfTasks.append(newTask)
//                    
//                }
//                
//               self.myTabelView.reloadData()
//            }
//            else {
//                println(error)
//            }
//        }
//        
        
    } // END OF loadData()
    
    func assignList(task: Task) {
    
        if task.dueDays == 0 {
            arrayOfLists[0].addTask(task)
        }else{
            arrayOfLists[1].addTask(task)
        }
        
        
    }
    
    
    
} // END OF VIEW CONTROLLER

