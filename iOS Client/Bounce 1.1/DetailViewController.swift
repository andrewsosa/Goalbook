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
    
    var taskString: String?
    var taskComp: Bool?
    var taskDate: String?
    var taskList: String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        taskName.text = taskString
        if taskComp == true{
            complete.text = "Completed"
        }
        else {
            complete.text = "Uncompleted"
        }
        
        
        topDrop.layer.shadowColor = UIColor.blackColor().CGColor
        topDrop.layer.shadowOffset = CGSizeMake(5, 5)
        topDrop.layer.shadowRadius = 5
        
        topDrop.layer.shadowOpacity = 0.4
        
    }
        // Do any additional setp after loading the view.
    

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func goBack(sender: AnyObject) {
        
        println("works")
        
    }
    
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
