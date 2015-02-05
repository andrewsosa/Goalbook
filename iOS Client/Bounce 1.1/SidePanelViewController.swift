//
//  SidePanelViewController.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/27/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit
import QuartzCore

var arrayOfLists:[List] = []
var item1 = List(name: "Dashboard")
var passLabel:String?

@objc protocol CenterViewControllerDelegate {
    optional func toggleLeftPanel()
    optional func collapseSidePanels()
}


class SidePanelViewController: UIViewController, UITableViewDataSource, UITableViewDelegate, CenterViewControllerDelegate {

    @IBOutlet weak var name: UILabel!
    
    @IBOutlet weak var greyBar: UIView!
    
    @IBOutlet weak var topBar: UIView!
    
    @IBOutlet weak var listEntry: UITextField!
    
    @IBOutlet weak var myTable: UITableView!
    
    @IBOutlet weak var logo: UIImageView!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        greyBar.layer.shadowColor = UIColor.blackColor().CGColor
        greyBar.layer.shadowOffset = CGSizeMake(5, 5)
        greyBar.layer.shadowRadius = 5
        greyBar.layer.shadowOpacity = 0.2
        
        logo.layer.borderWidth=5.0
        logo.layer.masksToBounds = false
        logo.layer.borderColor = UIColor.whiteColor().CGColor
        logo.layer.cornerRadius = 13
        logo.layer.cornerRadius = logo.frame.size.height/2
        logo.clipsToBounds = true

    }
    

    func CreatList() {
    
        if listEntry.text != "" {
        
            var tempString:String = listEntry.text
            
            var newList = List(name: tempString)
            
            arrayOfLists.append(newList)
            println(listEntry.text)
            listEntry.text = ""
            
            myTable.reloadData()
        }
        
    
    
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return arrayOfLists.count
        
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        var cell = UITableViewCell(style: UITableViewCellStyle.Default, reuseIdentifier: "Cell")
        
        let list:String = arrayOfLists[indexPath.row].listName!
        
        cell.textLabel?.text = list
        
        return cell
        
    }
    
    func textFieldShouldReturn(textField: UITextField!) -> Bool {
    
        CreatList()
        textField.resignFirstResponder()
        return true
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func tableView(tableView: UITableView, willSelectRowAtIndexPath indexPath: NSIndexPath) -> NSIndexPath? {
       
        println(delegate)
        
        passController!.currentList = arrayOfLists[indexPath.row]
        
        passController?.call()

        delegate?.toggleLeftPanel?()
        
        return indexPath
    }
    
    
    
   /*  func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        
        if(editingStyle == UITableViewCellEditingStyle.Delete) {
            
            arrayOfLists.removeAtIndex(indexPath.row)
            
            myTable.reloadData()
            
            println("removed")
        }
                    ************ THIS IS FOR WHEN I FIGURE OUT AUTOLAYOUT ********************
        
    } */

    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
