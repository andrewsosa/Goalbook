//
//  DetailViewController.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/20/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit

class DetailViewController: UIViewController {

    @IBOutlet weak var taskName: UILabel!
    
    @IBOutlet weak var complete: UILabel!
    
    @IBOutlet weak var topDrop: UIView!
    
    @IBOutlet weak var dateLabel: UILabel!
    
    @IBOutlet weak var listLabel: UILabel!
    
    @IBOutlet weak var stepper: UIStepper!
    
    @IBOutlet weak var valuelab: UILabel!
    
    @IBOutlet weak var discriptionBox: UITextView!
    
    var dueSave:Int?
    var currentTask:Task!
    var taskString: String?
    var taskComp: Bool?
    var taskDate: String?
    var taskList: String?
    var dueDays:Int?
    var compare = 0
    
    let currentDate = NSDate()
    var dueDate:NSDate?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        taskName.text = currentTask.taskName
        taskComp = currentTask.completed
        dueSave = currentTask.dueDays
        if taskComp == true{
            complete.text = "Completed"
        }
        else if taskComp == false{
            complete.text = "Uncompleted"
        }
        else {
            complete.text = "Broken"
        }
        
        listLabel.text = currentTask.list
        println(currentTask.list)
        topDrop.layer.shadowColor = UIColor.blackColor().CGColor
        topDrop.layer.shadowOffset = CGSizeMake(5, 5)
        topDrop.layer.shadowRadius = 5
        topDrop.layer.shadowOpacity = 0.4
     
        dateLabel.text = currentTask.dateToString()
        
        stepper.value = 0
        stepper.minimumValue = -1000.0
        stepper.maximumValue = 1000.0
    }
        // Do any additional setp after loading the view.
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func exit(sender: AnyObject) {
//        
//        var viewController: ViewController = self.storyboard?.instantiateViewControllerWithIdentifier("MainView") as! ViewController
//        self.presentViewController(viewController, animated: false, completion: nil)
        
    }
    
    override func viewWillAppear(animated: Bool) {
        currentTask = passTask!
        
    }
    
    @IBAction func stepperValueChanged(sender: AnyObject) {
        
        if compare < Int(stepper.value) {
            
            arrayOfTasks[detailPassNum!].dateUp()
            // currentTask.dateUp()
            
        } else if compare > Int(stepper.value) {
            var temp = currentTask.getDueDays
            
            arrayOfTasks[detailPassNum!].dateDown()
           // currentTask.dateDown()
            
        }
        
        compare = Int(stepper.value)
        dateLabel.text = currentTask.dateToString()
        
    }
    
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        
        if segue.identifier == "backToHome" {
            
            let vc:ViewController = segue.destinationViewController as! ViewController
            vc.currentList = listPass
            //vc.topBarColor()
            
            if dueSave == currentTask.dueDays {
            
            }
            else if ( dueSave == 0 && currentTask.dueDays != 0 ) {
                
                arrayOfLists[1].addTask(currentTask)
               // arrayOfLists[0].removeTask(currentTask)
                
            }
            else {
            
            println("DIDNT HAPPEN")
                
            }
        }
        
        
    }

    
    
        
    
}
