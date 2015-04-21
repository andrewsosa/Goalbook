//
//  ContainerViewController.swift
//  Bounce 1.1
//
//  Created by TJ Littlejohn on 1/27/15.
//  Copyright (c) 2015 TJ Littlejohn. All rights reserved.
//

import UIKit
import QuartzCore // tutorial had this not sure what it is

class ContainerViewController: UIViewController, CenterViewControllerDelegate {
    
    enum SlideOutState {
        case BothCollapsed
        case LeftPanelExpanded
    }
    
    var centerNavigationController: UINavigationController!
    var centerViewController: ViewController!
    
    var currentState: SlideOutState = .BothCollapsed {
        didSet {
            let shouldShowShadow = currentState != .BothCollapsed
            showShadowForCenterViewController(shouldShowShadow)
        }
    }
    
    var leftViewController: SidePanelViewController?

    let centerPanelExpandedOffset: CGFloat = 50
    
    func getObj() -> ContainerViewController {
    
        return self
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        
        centerViewController = UIStoryboard.centerViewController()
        delegate = self
        // centerViewController.delegate = self
        // wrap the centerViewController in a navigation controller, so we can push views to it
        // and display bar button items in the navigation bar
        centerNavigationController = UINavigationController(rootViewController: centerViewController)
        view.addSubview(centerNavigationController.view)
        addChildViewController(centerNavigationController)
        
        centerNavigationController.didMoveToParentViewController(self)
        
        addLeftPanelViewController()
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func toggleLeftPanel() {    // THIS IS A PROBLEM AREA
        
        let notAlreadyExpanded = (currentState == .BothCollapsed)
        addLeftPanelViewController()
        animateLeftPanel(shouldExpand: notAlreadyExpanded)

    }
    
    func addLeftPanelViewController() {
        if (leftViewController == nil) {
            leftViewController = UIStoryboard.leftViewController()
            
            addChildSidePanelController(leftViewController!)
        }
    }
    
    func addChildSidePanelController(sidePanelController: SidePanelViewController) {
        view.insertSubview(sidePanelController.view, atIndex: 0)
        println("child")
        addChildViewController(sidePanelController)
        sidePanelController.didMoveToParentViewController(self)
    }
    
    func animateLeftPanel(#shouldExpand: Bool) {
        if (shouldExpand) {
            println(currentState.hashValue)
            currentState = .LeftPanelExpanded
            animateCenterPanelXPosition(targetPosition: CGRectGetWidth(centerNavigationController.view.frame) - centerPanelExpandedOffset)
        } else {
            animateCenterPanelXPosition(targetPosition: 0) { finished in
                self.currentState = .BothCollapsed
                self.leftViewController!.view.removeFromSuperview() // MAYBE
                self.leftViewController = nil
            }
        }
    }
    
    func animateCenterPanelXPosition(#targetPosition: CGFloat, completion: ((Bool) -> Void)! = nil) {
        UIView.animateWithDuration(0.5, delay: 0, usingSpringWithDamping: 0.8, initialSpringVelocity: 0, options: .CurveEaseInOut, animations: {
            self.centerNavigationController.view.frame.origin.x = targetPosition
            }, completion: completion)
    }
    
    func showShadowForCenterViewController(shouldShowShadow: Bool) {
        if (shouldShowShadow) {
            centerNavigationController.view.layer.shadowOpacity = 0.8
        } else {
            centerNavigationController.view.layer.shadowOpacity = 0.0
        }
    }
    
} // END OF CLASS

private extension UIStoryboard {
    class func mainStoryboard() -> UIStoryboard { return UIStoryboard(name: "Main", bundle: NSBundle.mainBundle()) }
    
    class func leftViewController() -> SidePanelViewController? {
        return mainStoryboard().instantiateViewControllerWithIdentifier("LeftViewController") as? SidePanelViewController
    }
    
    class func centerViewController() -> ViewController? {
        return mainStoryboard().instantiateViewControllerWithIdentifier("MainView") as? ViewController
    }
}
