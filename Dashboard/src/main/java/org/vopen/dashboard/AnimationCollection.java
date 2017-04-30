package org.vopen.dashboard;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by dpandeli on 9/2/2015.
 */
public class AnimationCollection
{
    public static ObjectAnimator createXRotationAnimator(final View view)
    {
        return createXRotationAnimator(view, 0f, 360f);
    }

    public static ObjectAnimator createXRotationAnimator(final View view, float start, float end)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationY", start, end);
        animator.setDuration(200);

        return animator;
    }

    public static ObjectAnimator createYRotationAnimator(final View view)
    {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotationX", 0f, 360f);
        animator.setDuration(500);

        return animator;
    }

    public static void flipViewHorizontally(final View view, final View newView)
    {
        view.setVisibility(View.VISIBLE);
        newView.setVisibility(View.INVISIBLE);
        ObjectAnimator firstAnimation = createXRotationAnimator(view, 0f, 90f);
        final ObjectAnimator secondAnimation = createXRotationAnimator(newView, -90f, 0f);

        firstAnimation.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation)
            {
                view.setVisibility(View.INVISIBLE);
                newView.setVisibility(View.VISIBLE);
                secondAnimation.start();
            }

            @Override
            public void onAnimationCancel(Animator animation)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }
        });
        firstAnimation.start();
    }

    public static void flipViewVertically(View view)
    {
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(createXRotationAnimator(view, 0f, 45f), createXRotationAnimator(view, 45f, 90f));
        set.start();
    }
}
