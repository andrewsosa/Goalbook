//
//  CustomCell.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/19/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit

class CustomCell: UITableViewCell {

    @IBOutlet weak var mainLabel: UILabel!
    
    @IBOutlet weak var taskDate: UILabel!
    
    @IBOutlet weak var largeLabel: UILabel!
    
    @IBOutlet weak var checkBox: UIButton!
    
    @IBOutlet weak var check: UIImageView!
    
    @IBOutlet weak var animation: UIImageView!
    
    var timer = NSTimer()
    
    var counter = 1
    
    var cellNumber:Int?
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }
    
    func setCell(taskName: String){
    
        self.mainLabel.text = taskName
        
    }
    
    @IBAction func checked(sender: AnyObject) {
        passCheck = cellNumber
        PCTask = arrayOfTasks[cellNumber!]
        arrayOfLists[2].addTask(PCTask)
        passController!.currentList.arrayOfTasks[cellNumber!].Complete()
        arrayOfTasks[cellNumber!].Complete()
        timer = NSTimer.scheduledTimerWithTimeInterval(0.0178, target: self, selector: Selector("GoCheck"), userInfo: nil, repeats: true)
        
        println(arrayOfTasks[cellNumber!].taskName)
        println(arrayOfTasks[cellNumber!].completed)
    }
    
    func GoCheck() {
        if counter < 29 {
        animation.image = UIImage(named: "check-\(counter).png")
        counter++
        }
    }
    
    

    
}
